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

        fun obtenerSolicitudesParaTrabajo(): List<Solicitud>{
            val objConexion = ClaseConexion().cadenaConexion()
            //aqui obtengo el id de quien inicio sesion
            val idSolicitante = login.IdSolicitante
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
                a.NombreCategoria AS CategoriaTrabajo
            FROM SOLICITUD s
            INNER JOIN TRABAJO t ON s.IdTrabajo = t.IdTrabajo
            INNER JOIN AreaDeTrabajo a ON t.IdAreaDeTrabajo = a.IdAreaDeTrabajo
            WHERE s.IdSolicitante = '$idSolicitante'
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
