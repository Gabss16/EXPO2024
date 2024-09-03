package RecicleViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.Solicitud
import modelo.Trabajo

class AdaptadorSolicitud (var Datos : List<Solicitud>) : RecyclerView.Adapter<ViewHolderSolicitud>() {

    fun actualizarDatos(nuevosDatos: List<Solicitud>) {
        Datos= nuevosDatos
        notifyDataSetChanged()}

    fun actualizarEstadoSolicitud(idSolicitud: Int, nuevoEstado: String) {
        GlobalScope.launch(Dispatchers.IO) {
            // Crear un objeto de la clase de conexión
            val objConexion = ClaseConexion().cadenaConexion()

            // Consulta SQL para actualizar el estado de la solicitud

            val updateSolicitud = objConexion?.prepareStatement("UPDATE SOLICITUD SET Estado = ? WHERE IdSolicitud = ?") !!

            // Asignar parámetros a la consulta
            updateSolicitud.setString(1, nuevoEstado)
            updateSolicitud.setInt(2, idSolicitud)
            updateSolicitud.executeUpdate()

            val commit = objConexion.prepareStatement ("commit")
            commit.executeUpdate()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSolicitud {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_solicitudes, parent, false)
        return ViewHolderSolicitud(vista)
    }

    override fun getItemCount()= Datos.size

    override fun onBindViewHolder(holder: ViewHolderSolicitud, position: Int) {
        val Solicitud = Datos[position]

        holder.jobTitleTextView .text = Solicitud.TituloTrabajo
        holder.jobCategoryTextView.text = Solicitud.CategoriaTrabajo.toString()
        holder.statusTextView.text = Solicitud.Estado

        holder.acceptButton.setOnClickListener {

            actualizarEstadoSolicitud(Solicitud.IdSolicitud, "Activa")
        }

        holder.rejectButton.setOnClickListener {
            actualizarEstadoSolicitud(Solicitud.IdSolicitud, "Finalizada")
        }

    }
}