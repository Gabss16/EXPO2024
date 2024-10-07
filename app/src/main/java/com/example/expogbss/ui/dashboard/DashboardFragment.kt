package com.example.expogbss.ui.dashboard

import RecicleViewHelpers.AdaptadorMSolicitud
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.expogbss.FragmentPageAdapter
import com.example.expogbss.R
import com.example.expogbss.databinding.FragmentBuscarBinding
import com.example.expogbss.login
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Solicitud

class DashboardFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentPageAdapter
    private var _binding: FragmentBuscarBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentBuscarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rcvMSolis = root.findViewById<RecyclerView>(R.id.rcvMisSolicitudes)

        rcvMSolis.layoutManager = LinearLayoutManager(requireContext())

        fun obtenerSolicitudesParaTrabajo(): List<Solicitud> {
            val objConexion = ClaseConexion().cadenaConexion()
            // Aquí obtengo el id de quien inició sesión
            val idSolicitante = login.IdSolicitante
            // Creo un statement
            val statement = objConexion?.createStatement()

            val resultSet = statement?.executeQuery("""
     SELECT 
            s.IdSolicitud, 
            s.IdSolicitante, 
            ss.Nombre as NombreSolicitante,
            s.IdTrabajo, 
            s.FechaSolicitud, 
            s.Estado,
            t.Titulo AS TituloTrabajo,
            a.NombreAreaDeTrabajo AS CategoriaTrabajo
        FROM SOLICITUD s
        INNER JOIN TRABAJO t ON s.IdTrabajo = t.IdTrabajo
        INNER JOIN AreaDeTrabajo a ON t.IdAreaDeTrabajo = a.IdAreaDeTrabajo
        INNER JOIN SOLICITANTE ss ON s.IdSolicitante = ss.IdSolicitante
        WHERE s.IdSolicitante = '$idSolicitante'""")!!

            // En esta variable se añaden TODOS los valores de solicitudes
            val listaSolicitud = mutableListOf<Solicitud>()

            while (resultSet.next()) {
                val IdSolicitud = resultSet.getInt("IdSolicitud")
                val IdSolicitante = resultSet.getString("IdSolicitante")
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val FechaSolicitud = resultSet.getString("FechaSolicitud")
                val Estado = resultSet.getString("Estado")
                val tituloTrabajo = resultSet.getString("TituloTrabajo")
                val categoriaTrabajo = resultSet.getString("CategoriaTrabajo") // Ahora es el nombre del área
                val nombreSolicitante = resultSet.getString("NombreSolicitante") // Ahora es el nombre del solicitante

                val solicitud = Solicitud(
                    IdSolicitud,
                    IdSolicitante,
                    IdTrabajo,
                    FechaSolicitud,
                    Estado,
                    tituloTrabajo,
                    categoriaTrabajo,
                    nombreSolicitante
                )
                listaSolicitud.add(solicitud)
            }
            return listaSolicitud
        }


        // Configurar adaptador para solicitudes
        CoroutineScope(Dispatchers.IO).launch {
            val solicitudesDb = obtenerSolicitudesParaTrabajo()
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorMSolicitud(solicitudesDb)
                rcvMSolis.adapter = adapter
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
