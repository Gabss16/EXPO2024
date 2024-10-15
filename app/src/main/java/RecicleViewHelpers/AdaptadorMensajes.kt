package RecicleViewHelpers

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.R
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena.nombresSolicitante


data class Message(
    val senderId: String = "",
    val message: String = "",
)

class AdaptadorMensajes(private val messageList: List<Message>) : RecyclerView.Adapter<AdaptadorMensajes.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSenderId: TextView = view.findViewById(R.id.tvSenderId)
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_mensajes, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messageList[position]
        holder.tvSenderId.text = currentMessage.senderId
        holder.tvMessage.text = currentMessage.message
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}


