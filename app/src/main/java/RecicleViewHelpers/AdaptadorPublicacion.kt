package RecicleViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.DetallePublicacion
import com.example.expogbss.Detalle_Puesto
import com.example.expogbss.R
import modelo.Trabajo

class AdaptadorPublicacion(var Datos : List<Trabajo>) : RecyclerView.Adapter<ViewHolderPublicacion>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPublicacion {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_publicacion, parent, false)
        return ViewHolderPublicacion(vista)
    }

    override fun getItemCount()=Datos.size

    override fun onBindViewHolder(holder: ViewHolderPublicacion, position: Int) {
        val trabajos = Datos[position]
        holder.txtTIituloTrabajo.text = trabajos.Titulo
        holder.txtTipoTrabajo.text = trabajos.IdAreaDeTrabajo.toString()

        holder.itemView.setOnClickListener{

            val context = holder.itemView.context
            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalleP = Intent(context,DetallePublicacion ::class.java)

            //enviar a la otra pantalla todos mis valores
            pantallaDetalleP.putExtra("Titulo", trabajos.Titulo)
            pantallaDetalleP.putExtra("AreaDeTrabajo", trabajos.IdAreaDeTrabajo)
            pantallaDetalleP.putExtra("Descripcion", trabajos.Descripcion)
            pantallaDetalleP.putExtra("Direccion", trabajos.Direccion)
            pantallaDetalleP.putExtra("IdDepartamento", trabajos.IdDepartamento)
            pantallaDetalleP.putExtra("Experiencia", trabajos.Experiencia)
            pantallaDetalleP.putExtra("Requerimientos", trabajos.Requerimientos)
            pantallaDetalleP.putExtra("Estado", trabajos.Estado)
            pantallaDetalleP.putExtra("Salario", trabajos.Salario.toString()) // Para convertir de BigDecimal a cadena
            pantallaDetalleP.putExtra("Beneficios", trabajos.Beneficios)
            context.startActivity(pantallaDetalleP)

        }
    }
}