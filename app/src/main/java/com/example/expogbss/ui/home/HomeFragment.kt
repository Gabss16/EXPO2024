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
import com.example.expogbss.Domestico
import com.example.expogbss.Freelance
import com.example.expogbss.R
import com.example.expogbss.Salud
import com.example.expogbss.databinding.FragmentHomeBinding
import com.example.expogbss.delivery
import com.example.expogbss.educacion
import com.example.expogbss.hosteleria
import com.example.expogbss.sp
import com.example.expogbss.ventas
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
        val freelancer = root.findViewById<ImageButton>(R.id.BtnCategoria3)
        val domestico = root.findViewById<ImageButton>(R.id.BtnCategoria2)
        val delivery = root.findViewById<ImageButton>(R.id.BtnCategoria4)
        val salud = root.findViewById<ImageButton>(R.id.BtnCategoria5)
        val remoto = root.findViewById<ImageButton>(R.id.BtnCategoria10)
        val sp = root.findViewById<ImageButton>(R.id.BtnCategoria9)
        val cliente = root.findViewById<ImageButton>(R.id.BtnCategoria8)
        val educacion = root.findViewById<ImageButton>(R.id.BtnCategoria7)
        val hotel = root.findViewById<ImageButton>(R.id.BtnCategoria6)
        construccion.setOnClickListener{
            val intent = Intent(context, Construccion::class.java)
            startActivity(intent)
        }
        freelancer.setOnClickListener{
            val intent = Intent(context, Freelance::class.java)
            startActivity(intent)
        }

        domestico.setOnClickListener{
            val intent = Intent(context, Domestico::class.java)
            startActivity(intent)
        }
        delivery.setOnClickListener{
            val intent = Intent(context, delivery::class.java)
            startActivity(intent)
        }
        salud.setOnClickListener{
            val intent = Intent(context, Salud::class.java)
            startActivity(intent)
        }
        remoto.setOnClickListener{
            val intent = Intent(context, Construccion::class.java)
            startActivity(intent)
        }
        sp.setOnClickListener{
            val intent = Intent(context, sp::class.java)
            startActivity(intent)
        }
        cliente.setOnClickListener{
            val intent = Intent(context, ventas::class.java)
            startActivity(intent)
        }
        educacion.setOnClickListener{
            val intent = Intent(context, educacion::class.java)
            startActivity(intent)
        }
        hotel.setOnClickListener{
            val intent = Intent(context, hosteleria::class.java)
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