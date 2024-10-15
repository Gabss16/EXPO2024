package RecicleViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.expogbss.ChatEmpleador
import com.example.expogbss.ChatSolicitante
import com.example.expogbss.R
import modelo.Empleador
import modelo.Solicitante
import modelo.Trabajo

class AdaptadorChatsEmpleador(var Datos: List<Solicitante>): RecyclerView.Adapter<ViewHolderChats>() {

    private var datosOriginales: List<Solicitante> = Datos

    fun actualizarDatos(nuevosDatos: List<Solicitante>) {
        datosOriginales = nuevosDatos
        Datos= nuevosDatos
        notifyDataSetChanged()}



    fun filtrar(query: String) {
        Datos = if (query.isEmpty()) {
            datosOriginales
        } else {
            datosOriginales.filter {
                it.Nombre.contains(query, ignoreCase = true) ||
                        it.CorreoElectronico.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged() // Asegúrate de actualizar el RecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderChats {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_chats, parent, false)

        return ViewHolderChats(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderChats, position: Int) {
        val ChatEmp = Datos[position]
        holder.txtNombreChats.text = ChatEmp.Nombre
        holder.txtNombreEmpresaChats.text = ChatEmp.CorreoElectronico

        Glide.with(holder.itemView.context)
            .load(ChatEmp.Foto) // Asegúrate de que el modelo `Solicitante` tenga un campo `FotoUrl`
            .placeholder(R.drawable.fotoperfil) // Imagen de placeholder mientras se carga
            .error(R.drawable.fotoperfil) // Imagen en caso de error al cargar
            .into(holder.imgFotoChat)

        //click a la card

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            //Cambiar de pantalla a la pantalla de detalle
            val ChatsEmpleador = Intent(context, ChatEmpleador::class.java)
            //enviar a la otra pantalla todos mis valores
            ChatsEmpleador.putExtra("IdSolicitante", ChatEmp.IdSolicitante)
            ChatsEmpleador.putExtra("Nombre", ChatEmp.Nombre)
            ChatsEmpleador.putExtra("CorreoElectronico", ChatEmp.CorreoElectronico)
            context.startActivity(ChatsEmpleador)
        }
    }
}