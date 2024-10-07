package RecicleViewHelpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R
import modelo.Solicitud

class AdaptadorSolisAceptadas(var Datos : List<Solicitud>) : RecyclerView.Adapter<ViewHolderSolisAceptadas>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSolisAceptadas {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_solicitudes_aceptadas, parent, false)
        return ViewHolderSolisAceptadas(vista)
    }

    override fun getItemCount()= Datos.size

    override fun onBindViewHolder(holder: ViewHolderSolisAceptadas, position: Int) {

    }
}