package RecicleViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R
import modelo.Solicitud

class AdaptadorMSolicitud (var Datos : List<Solicitud>) : RecyclerView.Adapter<ViewHolderMSolicitud>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMSolicitud {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_estado_solicitud, parent, false)
        return ViewHolderMSolicitud(vista)
    }

    override fun getItemCount()= Datos.size

    override fun onBindViewHolder(holder: ViewHolderMSolicitud, position: Int) {
        val Solicitud = Datos[position]

        holder.jobTitleTextView2 .text = Solicitud.TituloTrabajo
        holder.statusTextView1.text = Solicitud.CategoriaTrabajo.toString()
        holder.statusTextView1.text = Solicitud.Estado
    }
}