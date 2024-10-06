package com.example.expogbss

import RecicleViewHelpers.AdaptadorChats
import RecicleViewHelpers.AdaptadorTrabajos
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Empleador
import modelo.Trabajo

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [chat.newInstance] factory method to
 * create an instance of this fragment.
 */
class chat : Fragment() {
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
        val root = inflater.inflate(R.layout.fragment_chat, container, false)

        val rcvChatSolicitante = root.findViewById<RecyclerView>(R.id.rcvChatSolicitante)
        rcvChatSolicitante.layoutManager = LinearLayoutManager(requireContext())

        fun obtenerDatos(): List<Empleador> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            //2 - Creo un statement
            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("""SELECT * FROM EMPLEADOR""")!!


            //en esta variable se añaden TODOS los valores de mascotas
            val listaEmpleadores = mutableListOf<Empleador>()

            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdEmpleador = resultSet.getString("IdEmpleador")
                val nombreEmpresa = resultSet.getString("NombreEmpresa")
                val nombreRepresentante = resultSet.getString("NombreRepresentante")
                val correoElectronico = resultSet.getString("CorreoElectronico")
                val numeroTelefono = resultSet.getString("NumeroTelefono")
                val direccion = resultSet.getString("Direccion")
                val idDepartamento = resultSet.getInt("IdDepartamento")
                val sitioWeb = resultSet.getString("SitioWeb")
                val estado = resultSet.getString("Estado")
                val foto = resultSet.getString("Foto")
                val contrasena = resultSet.getString("Contrasena")

                val empleador = Empleador(
                    IdEmpleador,
                    nombreEmpresa,
                    nombreRepresentante,
                    correoElectronico,
                    numeroTelefono,
                    direccion,
                    idDepartamento,
                    sitioWeb,
                    estado,
                    foto,
                    contrasena
                )
                listaEmpleadores.add(empleador)
            }
            return listaEmpleadores
        }

        CoroutineScope(Dispatchers.IO).launch {
            val EmpleadorDb = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorChats(EmpleadorDb)
                rcvChatSolicitante.adapter = adapter
            }
        }

                // Inflate the layout for this fragment
                return root

            }


            companion object {
            /**
             * Use this factory method to create a new instance of
             * this fragment using the provided parameters.
             *
             * @param param1 Parameter 1.
             * @param param2 Parameter 2.
             * @return A new instance of fragment chat.
             */
            // TODO: Rename and change types and number of parameters
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                chat().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
            }
        }