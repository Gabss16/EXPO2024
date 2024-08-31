package com.example.expogbss

import RecicleViewHelpers.AdaptadorChats
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

        val rcvChats = root.findViewById<RecyclerView>(R.id.rcvChats)

        rcvChats.layoutManager = LinearLayoutManager(requireContext())

        fun ObtenerChats(): List<Empleador>{

            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultset = statement?.executeQuery("SELECT * FROM EMPLEADOR")!!

            val listaDeChats = mutableListOf<Empleador>()

            while (resultset.next()){
                val IdEmpleador = resultset.getString("IdEmpleador")
                val NombreEmpresa = resultset.getString("NombreEmpresa")
                val NombreRepresentante = resultset.getString("NombreRepresentante")
                val CorreoElectronico = resultset.getString("CorreoElectronico")
                val NumeroTelefono = resultset.getString("NumeroTelefono")
                val Direccion = resultset.getString("Direccion")
                val IdDepartamento = resultset.getString("IdDepartamento")
                val SitioWeb = resultset.getString("SitioWeb")
                val Estado = resultset.getString("Estado")
                val Foto = resultset.getString("Foto")

                val valoresJuntos = Empleador(IdEmpleador,NombreEmpresa,NombreRepresentante,CorreoElectronico,NumeroTelefono,Direccion,IdDepartamento,SitioWeb,Foto, Estado)

                listaDeChats.add(valoresJuntos)
            }
return listaDeChats
        }

        CoroutineScope(Dispatchers.IO).launch {
            val chats = ObtenerChats()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorChats(chats)
                rcvChats.adapter = adapter
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