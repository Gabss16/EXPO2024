package RecicleViewHelpers

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.DetallePublicacion
import com.example.expogbss.Info_Perfil_Solicitante
import com.example.expogbss.R
import com.example.expogbss.ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.codigo
import com.example.expogbss.ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.correoIngresado
import com.example.expogbss.recuperarContrasena
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Solicitud
import modelo.Solicitante
import modelo.Trabajo

class AdaptadorSolicitud(var Datos: List<Solicitud>) : RecyclerView.Adapter<ViewHolderSolicitud>() {

    fun actualizarDatos(nuevosDatos: List<Solicitud>) {
        Datos = nuevosDatos
        notifyDataSetChanged()
    }

    fun eliminarDatos(idSolicitud: Int, nuevoEstado: String, posicion: Int) {
        //Actualizo la lista de datos y notifico al adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            //1- Creamos un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            val updateSolicitud =
                objConexion?.prepareStatement("UPDATE SOLICITUD SET Estado = ? WHERE IdSolicitud = ?")!!

            // Asignar parámetros a la consulta
            updateSolicitud.setString(1, nuevoEstado)
            updateSolicitud.setInt(2, idSolicitud)
            updateSolicitud.executeUpdate()
            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        // Notificar al adaptador sobre los cambios
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }


    fun actualizarEstadoSolicitud(idSolicitud: Int, nuevoEstado: String) {
        GlobalScope.launch(Dispatchers.IO) {
            // Crear un objeto de la clase de conexión
            val objConexion = ClaseConexion().cadenaConexion()

            // Consulta SQL para actualizar el estado de la solicitud

            val updateSolicitud =
                objConexion?.prepareStatement("UPDATE SOLICITUD SET Estado = ? WHERE IdSolicitud = ?")!!

            // Asignar parámetros a la consulta
            updateSolicitud.setString(1, nuevoEstado)
            updateSolicitud.setInt(2, idSolicitud)
            updateSolicitud.executeUpdate()
            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
            withContext(Dispatchers.Main) {
                val index = Datos.indexOfFirst { it.IdSolicitud == idSolicitud }
                if (index != -1) {
                    Datos[index].Estado = nuevoEstado
                    actualizarDatos(Datos)
                    notifyItemChanged(index) // Notificar que el elemento ha cambiado
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSolicitud {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_card_solicitudes, parent, false)
        return ViewHolderSolicitud(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderSolicitud, position: Int) {
        val Solicitud = Datos[position]


        val Nombresolicitante = Solicitud.NombreSolicitante

        // Obtener el correo electrónico del empleador
        val CorreoElectronico = obtenerCorreoElectronico(Solicitud.IdTrabajo)

        // Obtener el título del trabajo
        val Títulotrabajo = obtenerTituloTrabajo(Solicitud.IdTrabajo)

        val correo = obtenerCorreoSolicitante(Solicitud.IdSolicitante)

        fun generarHTMLCorreo(): String {
            return """
<html>
<body style="font-family: 'Roboto', sans-serif;
            background-color: #f5f7fa;
            margin: 0;
            padding: 0;">
    <div class="container" style="width: 100%;
            max-width: 600px; 
            margin: 50px auto;
            background-color: #ffffff;
            padding: 30px 20px;
            border-radius: 15px;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);">
        <div class="img" style="text-align: center;
            margin-top: 40px;">
            <img src="https://i.imgur.com/bXHJUmC.png" alt="Logo" width="400" style="border-radius: 10px;">
        </div>
        <div class="message" style="text-align: center;
            color: #2c3e50;
            margin-bottom: 40px;">
            <h2 style="font-size: 28px; 
            font-weight: 600;
            margin-bottom: 10px;">¡Felicidades!</h2>
            <p style="font-size: 18px; color: #7f8c8d;">
                Estimado usuario: <strong>$Nombresolicitante</strong>, le informamos que su solicitud al trabajo "<strong>$Títulotrabajo</strong>" fue aceptada. Para más información, póngase en contacto con el empleador <strong>$CorreoElectronico</strong>.
            </p>
        </div>
        <div class="footer-logo" style="text-align: center;
            margin-top: 40px;">
            <img src="https://i.imgur.com/TU8KAcy.png" alt="Logo" width="550" style="border-radius: 10px;">
        </div>
    </div>
</body>
</html>

""".trimIndent()
        }

        holder.jobTitleTextView.text = Solicitud.NombreSolicitante
        holder.jobCategoryTextView.text = Solicitud.CategoriaTrabajo.toString()
        holder.statusTextView.text = Solicitud.Estado

        holder.acceptButton.setOnClickListener {
            val context = holder.itemView.context
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que deseas aceptar la solicitud?")

            println("Este es el fokin correo $correo")

            builder.setPositiveButton("Aceptar") { dialog, _ ->

                val correoConHtml = generarHTMLCorreo()

                if (!correo.isNullOrEmpty()) {
                    GlobalScope.launch {
                        val correoEnviado = recuperarContrasena(
                            correo,
                            "Actualización sobre solicitud", correoConHtml
                        )
                        if (correoEnviado){
                            // Actualizar el estado de la solicitud a 'Aprobada'
                            actualizarEstadoSolicitud(Solicitud.IdSolicitud, "Aprobada")
                            dialog.dismiss() // Cerrar el diálogo
                        }
                    }
                } else {
                    // Maneja el caso en el que la variable correo es vacía
                    Toast.makeText(
                        context,
                        "No se pudo enviar el correo electrónico",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                // Cerrar el diálogo sin hacer nada
                dialog.dismiss()
            }

            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        }

        holder.rejectButton.setOnClickListener {
            val context = holder.itemView.context
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que deseas rechazar la solicitud?")

            builder.setPositiveButton("Rechazar") { dialog, _ ->
                // Actualizar el estado de la solicitud a 'Rechazada'
                actualizarEstadoSolicitud(Solicitud.IdSolicitud, "Rechazada")
                eliminarDatos(Solicitud.IdSolicitud, "Rechazada", position)
                dialog.dismiss()// Cerrar el diálogo

            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                // Cerrar el diálogo sin hacer nada
                dialog.dismiss()
            }

            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
        }

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context
            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalleP = Intent(context, Info_Perfil_Solicitante::class.java)

            pantallaDetalleP.putExtra("IdSolicitante", Solicitud.IdSolicitante)
            pantallaDetalleP.putExtra("NombreSolicitante", Solicitud.NombreSolicitante)

            context.startActivity(pantallaDetalleP)
        }
    }

    fun obtenerCorreoElectronico(idTrabajo: Int): String {
        // Realizar una consulta a la base de datos para obtener el correo electrónico del empleador
        val objConexion = ClaseConexion().cadenaConexion()
        val statement =
            objConexion?.prepareStatement("SELECT e.CorreoElectronico FROM Empleador e INNER JOIN Trabajo t ON e.IdEmpleador = t.IdEmpleador WHERE t.IdTrabajo = ?")
        statement?.setInt(1, idTrabajo)
        val resultSet = statement?.executeQuery()
        val correoElectronico = resultSet?.getString("CorreoElectronico")
        return correoElectronico ?: ""
    }

    fun obtenerTituloTrabajo(idTrabajo: Int): String {
        // Realizar una consulta a la base de datos para obtener el título del trabajo
        val objConexion = ClaseConexion().cadenaConexion()
        val statement =
            objConexion?.prepareStatement("SELECT t.Titulo FROM Trabajo t WHERE t.IdTrabajo = ?")
        statement?.setInt(1, idTrabajo)
        val resultSet = statement?.executeQuery()
        val tituloTrabajo = resultSet?.getString("Titulo")
        return tituloTrabajo ?: ""
    }

    fun obtenerCorreoSolicitante(idSolicitante: String): String {
        Log.d("obtenerCorreoSolicitante", "idSolicitante: $idSolicitante")
        val objConexion = ClaseConexion().cadenaConexion()
        val statement = objConexion?.prepareStatement("SELECT CorreoElectronico FROM Solicitante WHERE IdSolicitante = ?")
        statement?.setString(1, idSolicitante)
        val resultSet = statement?.executeQuery()
        Log.d("obtenerCorreoSolicitante", "resultSet: $resultSet")
        val correoSolicitante = resultSet?.getString("CorreoElectronico")
        Log.d("obtenerCorreoSolicitante", "correoSolicitante: $correoSolicitante")
        return correoSolicitante ?: ""
    }
}