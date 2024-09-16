package RecicleViewHelpers

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R

class ViewHolderPublicacion (view: View): RecyclerView.ViewHolder(view) {
    var txtTIituloTrabajo= view.findViewById<TextView>(R.id.txtTituloTrabajo)
    var txtTipoTrabajo =  view.findViewById<TextView>(R.id.txtTipoTrabajo)
    var imgBorrar =  view.findViewById<ImageButton>(R.id.imgBorrarPublicacion)
    var imgEditar =  view.findViewById<ImageButton>(R.id.imgEditarPublicacion)}