package RecicleViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.expogbss.ChatSolicitante
import com.example.expogbss.Detalle_Puesto
import com.example.expogbss.R
import modelo.Empleador
import modelo.Solicitante
import modelo.Trabajo


class AdaptadorChats(var Datos: List<Empleador>): RecyclerView.Adapter<ViewHolderChats>() {

    private var datosOriginales: List<Empleador> = Datos

    fun actualizarDatos(nuevosDatos: List<Empleador>) {
        datosOriginales = nuevosDatos
        Datos= nuevosDatos
        notifyDataSetChanged()}



    fun filtrar(query: String) {
        Datos = if (query.isEmpty()) {
            datosOriginales
        } else {
            datosOriginales.filter {
                it.NombreRepresentante.contains(query, ignoreCase = true)
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
        val ChatSoli = Datos[position]
        holder.txtNombreChats.text = ChatSoli.NombreRepresentante
        holder.txtNombreEmpresaChats.text = ChatSoli.NombreEmpresa

        Glide.with(holder.itemView.context)
            .load(ChatSoli.Foto) // Asegúrate de que el modelo `Solicitante` tenga un campo `FotoUrl`
            .placeholder(R.drawable.fotoperfil) // Imagen de placeholder mientras se carga
            .error(R.drawable.fotoperfil) // Imagen en caso de error al cargar
            .into(holder.imgFotoChat)

        //click a la card

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            //Cambiar de pantalla a la pantalla de detalle
            val ChatSolicitante = Intent(context, ChatSolicitante::class.java)
            //enviar a la otra pantalla todos mis valores
            ChatSolicitante.putExtra("IdEmpleador", ChatSoli.IdEmpleador)
            ChatSolicitante.putExtra("NombreEmpresa", ChatSoli.NombreEmpresa)
            ChatSolicitante.putExtra("NombreRepresentante", ChatSoli.NombreRepresentante)
            ChatSolicitante.putExtra("Foto", ChatSoli.Foto)
            context.startActivity(ChatSolicitante)
        }
    }
}