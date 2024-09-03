package com.example.expogbss

import RecicleViewHelpers.AdaptadorSolicitud
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import modelo.ClaseConexion
import modelo.Solicitud

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
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
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_first, container, false)

         lateinit var solicitudesAdapter: AdaptadorSolicitud

        val rcvMSolis = root.findViewById<RecyclerView>(R.id.rcvMisSolicitudes)

        rcvMSolis.layoutManager = LinearLayoutManager(requireContext())

        fun obtenerSolicitudesParaTrabajo(): List<Solicitud>{
            val objConexion = ClaseConexion().cadenaConexion()
            //2 - Creo un statement

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("""
        SELECT 
            s.IdSolicitud, 
            s.IdSolicitante, 
            s.IdTrabajo, 
            s.FechaSolicitud, 
            s.Estado,
            t.Titulo AS TituloTrabajo,
            t.IdAreaDeTrabajo AS CategoriaTrabajo
        FROM SOLICITUD s
        INNER JOIN TRABAJO t ON s.IdTrabajo = t.IdTrabajo
    """)!!

            //en esta variable se añaden TODOS los valores de mascotas
            val listaSolicitud = mutableListOf<Solicitud>()

            while (resultSet.next()) {
                val IdSolicitud = resultSet.getInt("IdSolicitud")
                val IdSolicitante = resultSet.getString("IdSolicitante")
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val FechaSolicitud = resultSet.getString("FechaSolicitud")
                val Estado = resultSet.getString("Estado")
                val tituloTrabajo = resultSet.getString("TituloTrabajo")
                val categoriaTrabajo = resultSet.getInt("CategoriaTrabajo")


                val solicitud = Solicitud(
                    IdSolicitud,
                    IdSolicitante,
                    IdTrabajo,
                    FechaSolicitud,
                    Estado,
                    tituloTrabajo,
                    categoriaTrabajo
                )
                listaSolicitud.add(solicitud)
            }
            return listaSolicitud

        }

        // Configurar adaptador para solicitudes
        // solicitudesAdapter = AdaptadorSolicitud(solicitudes)
        rcvMSolis.adapter = solicitudesAdapter

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}