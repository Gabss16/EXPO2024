package com.example.expogbss

import RecicleViewHelpers.AdaptadorChats
import RecicleViewHelpers.AdaptadorChatsEmpleador
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Empleador
import modelo.Solicitante

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

        val rcvChatEmpleador = view.findViewById<RecyclerView>(R.id.rcvChatEmpleador)
        rcvChatEmpleador.layoutManager = LinearLayoutManager(requireContext())

        fun obtenerDatos(): List<Solicitante> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            //2 - Creo un statement
            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.createStatement()

            val resultSet = statement?.executeQuery("""SELECT * FROM SOLICITANTE """)!!


            val listaSolicitante = mutableListOf<Solicitante>()

            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdSolicitante = resultSet.getString("IdSolicitante")
                val Nombre = resultSet.getString("Nombre")
                val CorreoElectronico = resultSet.getString("CorreoElectronico")
                val Telefono = resultSet.getString("Telefono")
                val Direccion = resultSet.getString("Direccion")
                val Latitud = resultSet.getDouble("Latitud")
                val Longitud = resultSet.getDouble("Longitud")
                val Departamento = resultSet.getInt("Departamento")
                val FechaDeNacimiento = resultSet.getString("FechaDeNacimiento")
                val Estado = resultSet.getString("Estado")
                val IdAreaDeTrabajo = resultSet.getInt("IdAreaDeTrabajo")
                val Habilidades = resultSet.getString("Habilidades")
                val foto = resultSet.getString("Foto")
                val Contrasena = resultSet.getString("Contrasena")

                val Solicitante = Solicitante(
                    IdSolicitante,
                    Nombre,
                    CorreoElectronico,
                    Telefono,
                    Direccion,
                    Latitud,
                    Longitud,
                    Departamento,
                    FechaDeNacimiento,
                    Estado,
                    IdAreaDeTrabajo,
                    Habilidades,
                    foto,
                    Contrasena
                )
                listaSolicitante.add(Solicitante)
            }
            return listaSolicitante
        }

        CoroutineScope(Dispatchers.IO).launch {
            val SolicitanteDb = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorChatsEmpleador(SolicitanteDb)
                rcvChatEmpleador.adapter = adapter
            }
        }
        return view
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