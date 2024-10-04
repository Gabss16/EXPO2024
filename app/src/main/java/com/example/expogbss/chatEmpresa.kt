package com.example.expogbss

import RecicleViewHelpers.AdaptadorMensajes
import RecicleViewHelpers.Message
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [chatEmpresa.newInstance] factory method to
 * create an instance of this fragment.
 */
class chatEmpresa : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var editTextMensaje: EditText
    private lateinit var buttonEnviar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: AdaptadorMensajes
    private val messageList = mutableListOf<Message>()
    private val chatId = "chatId1" // Reemplaza con el ID del chat correspondiente
    private val senderId = "IdEmpleador" // Reemplaza con el ID del usuario que envía el mensaje

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar la vista para el fragmento
        val view = inflater.inflate(R.layout.fragment_chat_empresa, container, false)

        // ESTO Configura el RecyclerView

        recyclerView = view.findViewById(R.id.recyclerViewMensajes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        messageAdapter = AdaptadorMensajes(messageList)
        recyclerView.adapter = messageAdapter

        escucharMensajes("chatId1")

        // Inicializa el EditText y el Button usando la vista inflada
        editTextMensaje = view.findViewById(R.id.editTextMensaje)
        buttonEnviar = view.findViewById(R.id.buttonEnviar)

        // Establece el listener para el botón
        buttonEnviar.setOnClickListener {
            // Obtén el texto del EditText
            val mensaje = editTextMensaje.text.toString()

            // Verifica que el mensaje no esté vacío
            if (mensaje.isNotBlank()) {
                // Llama a la función para enviar el mensaje
                enviarMensaje(chatId, senderId, mensaje)

                // Limpia el EditText después de enviar el mensaje
                editTextMensaje.text.clear()
            } else {
                Toast.makeText(requireContext(), "Por favor, escribe un mensaje.", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    private fun escucharMensajes(chatId: String) {
        val database = Firebase.database.reference
        val messagesRef = database.child("chats").child(chatId).child("messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear() // Limpiar la lista para agregar los mensajes actuales
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messageList.add(message)
                    }
                }
                // Notificar al adaptador que la lista ha cambiado
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener los mensajes", error.toException())
            }
        })
    }

    private fun enviarMensaje(chatId: String, senderId: String, mensaje: String) {
        val database = Firebase.database.reference
        val messageId = database.child("chats").child(chatId).child("messages").push().key

        val messageInfo = mapOf(
            "senderId" to senderId,
            "message" to mensaje,
            "timestamp" to System.currentTimeMillis()
        )

        if (messageId != null) {
            database.child("chats").child(chatId).child("messages").child(messageId).setValue(messageInfo)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment chatEmpresa.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            chatEmpresa().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}