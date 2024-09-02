package com.example.expogbss.ui.home

import RecicleViewHelpers.AdaptadorTrabajos
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.Construccion
import com.example.expogbss.R
import com.example.expogbss.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Trabajo

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val construccion = root.findViewById<ImageButton>(R.id.BtnCategoria1)
        construccion.setOnClickListener{
            val intent = Intent(context, Construccion::class.java)
            startActivity(intent)
        }

        val rcvTrabajosPublicados = root.findViewById<RecyclerView>(R.id.rcvTrabajosPublicados)
        rcvTrabajosPublicados.layoutManager = LinearLayoutManager(requireContext())

        fun obtenerDatos(): List<Trabajo> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            //2 - Creo un statement
            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM TRABAJO")!!


            //en esta variable se añaden TODOS los valores de mascotas
            val listaTrabajos = mutableListOf<Trabajo>()

            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val IdEmpleador = resultSet.getString("IdEmpleador")
                val IdAreaDeTrabajo = resultSet.getInt("IdAreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Direccion = resultSet.getString("Direccion")
                val IdDepartamento = resultSet.getInt("IdDepartamento")
                val Experiencia = resultSet.getString("Experiencia")
                val Requerimientos = resultSet.getString("Requerimientos")
                val Estado = resultSet.getString("Estado")
                val Salario = resultSet.getBigDecimal("Salario")
                val Beneficios = resultSet.getString("Beneficios")
                val FechaDePublicacion = resultSet.getDate("FechaDePublicacion")

                val trabajo = Trabajo(
                    IdTrabajo,
                    Titulo,
                    IdEmpleador,
                    IdAreaDeTrabajo,
                    Descripcion,
                    Direccion,
                    IdDepartamento,
                    Experiencia,
                    Requerimientos,
                    Estado,
                    Salario,
                    Beneficios,
                    FechaDePublicacion
                )
                listaTrabajos.add(trabajo)
            }
            return listaTrabajos


        }

        CoroutineScope(Dispatchers.IO).launch {
            val TrabajoDb = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorTrabajos(TrabajoDb)
                rcvTrabajosPublicados.adapter = adapter
            }
        }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}