package RecicleViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R

class ViewHolderChats(view: View): RecyclerView.ViewHolder(view) {

    val txtNombreChats = view.findViewById<TextView>(R.id.txtNombreChat)
    val txtNombreEmpresaChats = view.findViewById<TextView>(R.id.txtNombreEmpresaChat)
    val imgFotoChat = view.findViewById<ImageView>(R.id.imgFotoChat)

}