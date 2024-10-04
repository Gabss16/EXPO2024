package RecicleViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.DetallePublicacion
import com.example.expogbss.Detalle_Puesto
import com.example.expogbss.R
import com.example.expogbss.editar_perfil_Empleador
import com.example.expogbss.editar_publicacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.Trabajo


class AdaptadorPublicacionReactivar(var Datos : List<Trabajo>) : RecyclerView.Adapter<ViewHolderPublicacionReactivar>() {



    fun eliminarDatos(TituloTrabajo: String, posicion: Int) {
        //Actualizo la lista de datos y notifico al adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            //1- Creamos un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Crear una variable que contenga un PrepareStatement
            val updatePublicacion =
                objConexion?.prepareStatement("UPDATE TRABAJO SET Estado = 'Activo' WHERE Titulo = ?")!!
            updatePublicacion.setString(1, TituloTrabajo)
            updatePublicacion.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        // Notificar al adaptador sobre los cambios
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPublicacionReactivar {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_trabajos_eliminados, parent, false)
        return ViewHolderPublicacionReactivar(vista)
    }

    override fun getItemCount()=Datos.size

    override fun onBindViewHolder(holder: ViewHolderPublicacionReactivar, position: Int) {
        val trabajos = Datos[position]
        holder.txtTIituloTrabajoReactivar.text = trabajos.Titulo
        holder.txtTipoTrabajoReactivar.text = trabajos.NombreAreaDeTrabajo.toString()

        //todo: clic al icono de eliminar
        holder.imgReactivarPublicacion.setOnClickListener {

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Reactivar Trabajo")
            builder.setMessage("¿Desea Reactivar el trabajo?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(trabajos.Titulo, position)
            }

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }


    }
}