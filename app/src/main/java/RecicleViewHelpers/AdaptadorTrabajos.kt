package RecicleViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.expogbss.Detalle_Puesto
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
        holder.txtTipoTrabajo.text = trabajos.IdAreaDeTrabajo.toString()

        //click a la card

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalle = Intent(context, Detalle_Puesto::class.java)
            //enviar a la otra pantalla todos mis valores
            pantallaDetalle.putExtra("Titulo", trabajos.Titulo)
            pantallaDetalle.putExtra("AreaDeTrabajo", trabajos.IdAreaDeTrabajo)
            pantallaDetalle.putExtra("Descripcion", trabajos.Descripcion)
            pantallaDetalle.putExtra("Ubicacion", trabajos.Ubicacion)
            pantallaDetalle.putExtra("Experiencia", trabajos.Experiencia)
            pantallaDetalle.putExtra("Requerimientos", trabajos.Requerimientos)
            pantallaDetalle.putExtra("Estado", trabajos.Estado)
            pantallaDetalle.putExtra("Salario", trabajos.Salario.toString()) // Para convertir de BigDecimal a cadena
            pantallaDetalle.putExtra("Beneficios", trabajos.Beneficios)
            context.startActivity(pantallaDetalle)
        }
    }
}