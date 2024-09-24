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

    private var datosOriginales: List<Trabajo> = Datos

    fun actualizarDatos(nuevosDatos: List<Trabajo>) {
        Datos= nuevosDatos
        notifyDataSetChanged()}

    fun filtrar(query: String) {
        Datos = if (query.isEmpty()) {
            datosOriginales
        } else {
            datosOriginales.filter {
                it.Titulo.contains(query, ignoreCase = true) || // Filtra por Titulo
                        it.Descripcion.contains(query, ignoreCase = true) // Filtra por Descripción
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTrabajos {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_trabajos_empresa, parent, false)
        return ViewHolderTrabajos(vista)



    }

    override fun getItemCount() =Datos.size


    override fun onBindViewHolder(holder: ViewHolderTrabajos, position: Int) {
        val trabajos = Datos[position]
        holder.txtTIituloTrabajo.text = trabajos.Titulo
        holder.txtTipoTrabajo.text = trabajos.NombreAreaDeTrabajo.toString()

        //click a la card

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalle = Intent(context, Detalle_Puesto::class.java)
            //enviar a la otra pantalla todos mis valores
            pantallaDetalle.putExtra("IdTrabajo", trabajos.IdTrabajo)
            pantallaDetalle.putExtra("Titulo", trabajos.Titulo)
            pantallaDetalle.putExtra("NombreAreaDeTrabajo", trabajos.NombreAreaDeTrabajo)
            pantallaDetalle.putExtra("Descripcion", trabajos.Descripcion)
            pantallaDetalle.putExtra("Direccion", trabajos.Direccion)
            pantallaDetalle.putExtra("IdDepartamento", trabajos.IdDepartamento)
            pantallaDetalle.putExtra("Experiencia", trabajos.Experiencia)
            pantallaDetalle.putExtra("Requerimientos", trabajos.Requerimientos)
            pantallaDetalle.putExtra("Estado", trabajos.Estado)
            pantallaDetalle.putExtra("Salario", trabajos.Salario.toString()) // Para convertir de BigDecimal a cadena
            pantallaDetalle.putExtra("Beneficios", trabajos.Beneficios)
            context.startActivity(pantallaDetalle)
        }
    }
}