package com.example.expogbss

import RecicleViewHelpers.AdaptadorTrabajos
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Trabajo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeEmpresa.newInstance] factory method to
 * create an instance of this fragment.
 */
class homeEmpresa : Fragment() {
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

        val root = inflater.inflate(R.layout.fragment_home_empresa, container, false)

        // initializing our variable for button with its id.
        val btnShowBottomSheet = root.findViewById<ImageButton>(R.id.idBtnShowBottomSheet)
        val rcvTrabajos = root.findViewById<RecyclerView>(R.id.rcvTrabajos)

        rcvTrabajos.layoutManager = LinearLayoutManager(requireContext())

        fun obtenerDatos() : List<Trabajo>{
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
            while (resultSet.next()){
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val IdEmpleador = resultSet.getString("IdEmpleador")
                val AreaDeTrabajo = resultSet.getString("AreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Ubicacion = resultSet.getString("Ubicacion")
                val Experiencia = resultSet.getString("Experiencia")
                val Requerimientos = resultSet.getString("Requerimientos")
                val Estado = resultSet.getString("Estado")
                val Salario = resultSet.getInt("Salario")
                val Beneficios = resultSet.getString("fecha_finBeneficios_ticket")
                val FechaDePublicacion = resultSet.getDate("FechaDePublicacion")

                val trabajo = Trabajo(IdTrabajo,Titulo,IdEmpleador,AreaDeTrabajo,Descripcion,Ubicacion, Experiencia,Requerimientos,Estado,Salario,Beneficios,FechaDePublicacion)
                listaTrabajos.add(trabajo)
            }
            return listaTrabajos


        }

        CoroutineScope(Dispatchers.IO).launch {
            val TrabajoDb = obtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = AdaptadorTrabajos(TrabajoDb)
                rcvTrabajos.adapter = adapter
            }
        }

        // adding on click listener for our button.
        btnShowBottomSheet.setOnClickListener {

            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(requireContext())

            // on below line we are inflating a layout file which we have created.
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)


            // on below line we are creating a variable for our button
            // which we are using to dismiss our dialog.
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
            val txtTituloJob = view.findViewById<EditText>(R.id.txtTituloJob)
            val txtUbicacionJob = view.findViewById<EditText>(R.id.txtUbicacionJob)
            val txtDescripcionJob = view.findViewById<EditText>(R.id.txtDescripcionJob)
            val txtExperienciaJob = view.findViewById<EditText>(R.id.txtExperienciaJob)
            val txtHabilidadesJob = view.findViewById<EditText>(R.id.txtHabilidadesJob)
            val txtBeneficiosJob = view.findViewById<EditText>(R.id.txtBeneficiosJob)
            val txtSalarioJob = view.findViewById<EditText>(R.id.txtSalarioJob)




            val uuid = UUID.randomUUID().toString()
            val fechaDePublicacion = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val spnTiposTrabajo = view.findViewById<Spinner>(R.id.spnTiposTrabajo)
            val listadoAreas = listOf(
                "Trabajo doméstico",
                "Freelancers",
                "Trabajos remotos",
                "Servicios de entrega",
                "Sector de la construcción",
                "Área de la salud",
                "Sector de la hostelería",
                "Servicios profesionales",
                "Área de ventas y atención al cliente",
                "Educación y enseñanza"
            )
            val adaptadorAreasDeTrabajo =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listadoAreas)
            spnTiposTrabajo.adapter = adaptadorAreasDeTrabajo

             fun obtenerIdEmpleador(): String {
                return login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
            }

            val idEmpleador = obtenerIdEmpleador()




            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {

                CoroutineScope(Dispatchers.IO).launch {
                    //1-creo un objeto de la clse conexion
                    val objConexion = ClaseConexion().cadenaConexion()

                    //2-creo una variable que contenga un PrepareStatement
                    val addTrabajo =
                        objConexion?.prepareStatement("INSERT INTO TRABAJO ( Titulo , IdEmpleador , AreaDeTrabajo,Descripcion ,Ubicacion , Experiencia , Requerimientos , Estado ,Salario , Beneficios ) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )")!!
                    addTrabajo.setString(2, txtTituloJob.text.toString())
                    addTrabajo.setString(3, idEmpleador)
                    addTrabajo.setString(4, spnTiposTrabajo.selectedItem.toString())
                    addTrabajo.setString(5, txtDescripcionJob.text.toString())
                    addTrabajo.setString(6, txtUbicacionJob.text.toString())
                    addTrabajo.setString(7, txtExperienciaJob.text.toString())
                    addTrabajo.setString(8, txtHabilidadesJob.text.toString())
                    addTrabajo.setString(9, "Activo")
                    addTrabajo.setString(10, txtSalarioJob.text.toString())
                    addTrabajo.setString(11, txtBeneficiosJob.text.toString())


                    addTrabajo.executeUpdate()
                    Toast.makeText(requireContext(), "Trabajo Ingresado", Toast.LENGTH_LONG).show()

                    // on below line we are calling a dismiss
                    // method to close our dialog.
                    dialog.dismiss()
                }
                // below line is use to set cancelable to avoid
                // closing of dialog box when clicking on the screen.
                dialog.setCancelable(false)

                // on below line we are setting
                // content view to our view.
                dialog.setContentView(view)

                // on below line we are calling
                // a show method to display a dialog.
                dialog.show()
            }
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homeEmpresa.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeEmpresa().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
