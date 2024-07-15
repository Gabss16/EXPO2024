package RecicleViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.expogbss.R



import modelo.Trabajo

class AdaptadorTrabajos(var Datos : List<Trabajo>) : RecyclerView.Adapter<ViewHolderTrabajos>() {

    fun actualizarDatos(nuevosDatos: List<Trabajo>) {
        Datos= nuevosDatos
        notifyDataSetChanged()}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTrabajos {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_trabajos_empresa, parent, false)
        return ViewHolderTrabajos(vista)



    }

    override fun getItemCount() =Datos.size


    override fun onBindViewHolder(holder: ViewHolderTrabajos, position: Int) {
        val trabajos = Datos[position]
        holder.txtTIituloTrabajo.text = trabajos.Titulo
        holder.txtTipoTrabajo.text = trabajos.AreaDeTrabajo
    }
}