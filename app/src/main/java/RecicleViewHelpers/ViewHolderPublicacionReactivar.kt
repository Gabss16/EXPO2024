package RecicleViewHelpers

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R
//TODO Adaptar a nueva lógica

class ViewHolderPublicacionReactivar(view: View): RecyclerView.ViewHolder(view) {
    var txtTIituloTrabajoReactivar= view.findViewById<TextView>(R.id.txtTituloTrabajoReactivar)
    var txtTipoTrabajoReactivar =  view.findViewById<TextView>(R.id.txtTipoTrabajoReactivar)
    var imgReactivarPublicacion =  view.findViewById<ImageButton>(R.id.imgReactivarTrabajo)}