package RecicleViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.Info_Perfil_Solicitante
import com.example.expogbss.R
import modelo.Solicitud

class AdaptadorSolisAceptadas(var Datos : List<Solicitud>) : RecyclerView.Adapter<ViewHolderSolisAceptadas>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSolisAceptadas {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_solicitudes_aceptadas, parent, false)
        return ViewHolderSolisAceptadas(vista)
    }

    override fun getItemCount()= Datos.size

    override fun onBindViewHolder(holder: ViewHolderSolisAceptadas, position: Int) {
        val Solicitud = Datos[position]

        holder.jobTitleTextView3 .text = Solicitud.NombreSolicitante
        holder.jobCategoryTextView3.text = Solicitud.CategoriaTrabajo.toString()
        holder.statusTextView3.text = Solicitud.Estado


        holder.itemView.setOnClickListener {

            val context = holder.itemView.context
            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalleP = Intent(context, Info_Perfil_Solicitante::class.java)

            pantallaDetalleP.putExtra("IdSolicitante", Solicitud.IdSolicitante)
            pantallaDetalleP.putExtra("NombreSolicitante", Solicitud.NombreSolicitante)

            context.startActivity(pantallaDetalleP)
        }
    }
}