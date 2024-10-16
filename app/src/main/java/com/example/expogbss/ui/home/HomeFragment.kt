package com.example.expogbss.ui.home

import RecicleViewHelpers.AdaptadorTrabajos
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expogbss.Construccion
import com.example.expogbss.Domestico
import com.example.expogbss.eeducacion
import com.example.expogbss.Freelance
import com.example.expogbss.R
import com.example.expogbss.Remoto
import com.example.expogbss.Salud
import com.example.expogbss.Servicios
import com.example.expogbss.databinding.FragmentHomeBinding
import com.example.expogbss.hosteleria
import com.example.expogbss.ventas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Trabajo
import com.example.expogbss.ddelivery


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

        val txtBuscar = root.findViewById<EditText>(R.id.txtBuscar)


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
            val intent = Intent(context, ddelivery::class.java)
            startActivity(intent)
        }
        salud.setOnClickListener{
            val intent = Intent(context, Salud::class.java)
            startActivity(intent)
        }
        remoto.setOnClickListener{
            val intent = Intent(context, Remoto::class.java)
            startActivity(intent)
        }
        sp.setOnClickListener{
            val intent = Intent(context, Servicios::class.java)
            startActivity(intent)
        }
        cliente.setOnClickListener{
            val intent = Intent(context, ventas::class.java)
            startActivity(intent)
        }
        educacion.setOnClickListener{
            val intent = Intent(context, eeducacion::class.java)
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
            val resultSet = statement?.executeQuery("""    SELECT 
    T.IdTrabajo, 
    T.Titulo, 
    T.IdEmpleador, 
    E.NombreRepresentante,  -- Agrega el nombre del representante
    A.NombreAreaDetrabajo AS NombreAreaDeTrabajo, 
    T.Descripcion,   
    T.Direccion, 
    T.Longitud,
    T.Latitud,
    T.IdDepartamento, 
    T.Experiencia, 
    T.Requerimientos, 
    T.Estado, 
    T.SalarioMinimo,
    T.SalarioMaximo,
    T.Beneficios, 
    T.FechaDePublicacion
FROM 
    TRABAJO T
INNER JOIN AreaDeTrabajo A ON  T.IdAreaDeTrabajo = A.IdAreaDeTrabajo
    INNER JOIN 
    EMPLEADOR E ON T.IdEmpleador = E.IdEmpleador
 WHERE T.Estado = 'Activo'""")!!


            //en esta variable se añaden TODOS los valores de mascotas
            val listaTrabajos = mutableListOf<Trabajo>()

            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val IdEmpleador = resultSet.getString("IdEmpleador")
                val NombreRepresentante = resultSet.getString("NombreRepresentante")
                val NombreAreaDeTrabajo  = resultSet.getString("NombreAreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Direccion = resultSet.getString("Direccion")
                val Longitud = resultSet.getDouble("Longitud")
                val Latitud = resultSet.getDouble("Latitud")
                val IdDepartamento = resultSet.getInt("IdDepartamento")
                val Experiencia = resultSet.getString("Experiencia")
                val Requerimientos = resultSet.getString("Requerimientos")
                val Estado = resultSet.getString("Estado")
                val SalarioMinimo = resultSet.getBigDecimal("SalarioMinimo")
                val SalarioMaximo = resultSet.getBigDecimal("SalarioMaximo")
                val Beneficios = resultSet.getString("Beneficios")
                val FechaDePublicacion = resultSet.getDate("FechaDePublicacion")

                val trabajo = Trabajo(
                    IdTrabajo,
                    Titulo,
                    NombreRepresentante,
                    NombreAreaDeTrabajo,
                    Descripcion,
                    Direccion,
                    Longitud,
                    Latitud,
                    IdDepartamento,
                    Experiencia,
                    Requerimientos,
                    Estado,
                    SalarioMinimo,
                    SalarioMaximo,
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

                txtBuscar.addTextChangedListener(object : TextWatcher{
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                        adapter.filtrar(s.toString())

                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }
        }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}