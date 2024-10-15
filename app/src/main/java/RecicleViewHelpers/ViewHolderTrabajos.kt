package RecicleViewHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R

class  ViewHolderTrabajos(view: View): RecyclerView.ViewHolder(view) {
    var txtTipoTrabajo= view.findViewById<TextView>(R.id.txtTipoTrabajo)
    var txtTIituloTrabajo =  view.findViewById<TextView>(R.id.txtTIituloTrabajoo)
    var txtPublicador= view.findViewById<TextView>(R.id.txtPublicador)
    var txtFexhaa= view.findViewById<TextView>(R.id.txtFexhaa)



}