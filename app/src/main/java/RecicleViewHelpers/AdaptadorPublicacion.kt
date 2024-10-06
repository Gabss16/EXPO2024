package RecicleViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.DetallePublicacion
import com.example.expogbss.Detalle_Puesto
import com.example.expogbss.R
import com.example.expogbss.editar_perfil_Empleador
import com.example.expogbss.editar_publicacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.Trabajo

class AdaptadorPublicacion(var Datos : List<Trabajo>) : RecyclerView.Adapter<ViewHolderPublicacion>() {

    fun actualizarDatos(nuevosDatos: List<Trabajo>) {
        Datos= nuevosDatos
        notifyDataSetChanged()}

    fun eliminarDatos(TituloTrabajo: String, posicion: Int) {
        //Actualizo la lista de datos y notifico al adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            //1- Creamos un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Crear una variable que contenga un PrepareStatement
            val updatePublicacion =
                objConexion?.prepareStatement("UPDATE TRABAJO SET Estado = 'Inactivo' WHERE Titulo = ?")!!
            updatePublicacion.setString(1, TituloTrabajo)
            updatePublicacion.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        // Notificar al adaptador sobre los cambios
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPublicacion {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_publicacion, parent, false)
        return ViewHolderPublicacion(vista)
    }

    override fun getItemCount()=Datos.size

    override fun onBindViewHolder(holder: ViewHolderPublicacion, position: Int) {
        val trabajos = Datos[position]
        holder.txtTIituloTrabajo.text = trabajos.Titulo
        holder.txtTipoTrabajo.text = trabajos.NombreAreaDeTrabajo.toString()

        //todo: clic al icono de eliminar
        holder.imgBorrar.setOnClickListener {

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar el trabajo?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(trabajos.Titulo, position)
            }

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        //Todo: icono de editar
        holder.imgEditar.setOnClickListener{
            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val PantallaEditar = Intent(context, editar_publicacion::class.java)

            PantallaEditar.putExtra("IdTrabajo", trabajos.IdTrabajo) // Pasas el IdTrabajo

            PantallaEditar.putExtra("Titulo", trabajos.Titulo)
            PantallaEditar.putExtra("NombreAreaDeTrabajo", trabajos.NombreAreaDeTrabajo)
            PantallaEditar.putExtra("Descripcion", trabajos.Descripcion)
            PantallaEditar.putExtra("Direccion", trabajos.Direccion)
            PantallaEditar.putExtra("IdDepartamento", trabajos.IdDepartamento)
            PantallaEditar.putExtra("Experiencia", trabajos.Experiencia)
            PantallaEditar.putExtra("Requerimientos", trabajos.Requerimientos)
            PantallaEditar.putExtra("Estado", trabajos.Estado)
            PantallaEditar.putExtra("Salario", trabajos.Salario.toString()) // Para convertir de BigDecimal a cadena
            PantallaEditar.putExtra("Beneficios", trabajos.Beneficios)
            context.startActivity(PantallaEditar)
        }

        //Todo: Clic a la card completa
        holder.itemView.setOnClickListener{

            val context = holder.itemView.context
            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalleP = Intent(context,DetallePublicacion ::class.java)

            //enviar a la otra pantalla todos mis valores
            pantallaDetalleP.putExtra("IdTrabajo", trabajos.IdTrabajo)
            pantallaDetalleP.putExtra("Titulo", trabajos.Titulo)
            pantallaDetalleP.putExtra("NombreAreaDeTrabajo", trabajos.NombreAreaDeTrabajo)
            pantallaDetalleP.putExtra("Descripcion", trabajos.Descripcion)
            pantallaDetalleP.putExtra("Direccion", trabajos.Direccion)
            pantallaDetalleP.putExtra("IdDepartamento", trabajos.IdDepartamento)
            pantallaDetalleP.putExtra("Experiencia", trabajos.Experiencia)
            pantallaDetalleP.putExtra("Requerimientos", trabajos.Requerimientos)
            pantallaDetalleP.putExtra("Estado", trabajos.Estado)
            pantallaDetalleP.putExtra("Salario", trabajos.Salario.toString()) // Para convertir de BigDecimal a cadena
            pantallaDetalleP.putExtra("Beneficios", trabajos.Beneficios)
            context.startActivity(pantallaDetalleP)

        }
    }
}