package RecicleViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R
import modelo.Solicitud
import modelo.Trabajo

class AdaptadorSolicitud (var Datos : List<Solicitud>) : RecyclerView.Adapter<ViewHolderSolicitud>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSolicitud {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_solicitudes, parent, false)
        return ViewHolderSolicitud(vista)
    }

    override fun getItemCount()= Datos.size

    override fun onBindViewHolder(holder: ViewHolderSolicitud, position: Int) {
        val Solicitud = Datos[position]

        holder.jobTitleTextView .text = Solicitud.IdTrabajo.toString()
        holder.jobCategoryTextView.text = Solicitud.IdTrabajo.toString()
        holder.statusTextView.text = Solicitud.Estado


    }
}