package RecicleViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.expogbss.R
import modelo.Empleador
import modelo.Solicitante



class AdaptadorChats(var Datos: List<Empleador>): RecyclerView.Adapter<ViewHolderChats>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderChats {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_chats, parent, false)

        return ViewHolderChats(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderChats, position: Int) {
        val item = Datos[position]
        holder.txtNombreChats.text = item.NombreEmpresa
    }
}
