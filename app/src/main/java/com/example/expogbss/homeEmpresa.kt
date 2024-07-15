package com.example.expogbss

import RecicleViewHelpers.AdaptadorTrabajos
import android.os.Bundle
import android.util.Log
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
import java.math.BigDecimal
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

        fun obtenerDatos(): List<Trabajo> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            //2 - Creo un statement
            fun obtenerIdEmpleador(): String {
                return login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
            }
            val idEmpleador = obtenerIdEmpleador()

            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.prepareStatement("SELECT * FROM TRABAJO WHERE IdEmpleador = ?")
            statement?.setString(1, idEmpleador)
            val resultSet = statement?.executeQuery()!!


            //en esta variable se añaden TODOS los valores de mascotas
            val listaTrabajos = mutableListOf<Trabajo>()



            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val AreaDeTrabajo = resultSet.getString("AreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Ubicacion = resultSet.getString("Ubicacion")
                val Experiencia = resultSet.getString("Experiencia")
                val Requerimientos = resultSet.getString("Requerimientos")
                val Estado = resultSet.getString("Estado")
                val Salario = resultSet.getBigDecimal("Salario")
                val Beneficios = resultSet.getString("Beneficios")
                val FechaDePublicacion = resultSet.getDate("FechaDePublicacion")

                val trabajo = Trabajo(
                    IdTrabajo,
                    Titulo,
                    idEmpleador,
                    AreaDeTrabajo,
                    Descripcion,
                    Ubicacion,
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


            val fechaDePublicacion =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

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
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    listadoAreas
                )
            spnTiposTrabajo.adapter = adaptadorAreasDeTrabajo

            fun obtenerIdEmpleador(): String {
                return login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
            }

            val idEmpleador = obtenerIdEmpleador()
            Log.d("InsertJob", "IdEmpleador obtenido: $idEmpleador")


            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {

                if (txtTituloJob.text.isEmpty() || txtUbicacionJob.text.isEmpty() || txtDescripcionJob.text.isEmpty() ||
                    txtExperienciaJob.text.isEmpty() || txtHabilidadesJob.text.isEmpty() || txtBeneficiosJob.text.isEmpty() ||
                    txtSalarioJob.text.isEmpty()) {

                    Toast.makeText(requireContext(), "Todos los campos deben estar llenos", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val salarioText = txtSalarioJob.text.toString()
                if (!salarioText.matches(Regex("^\\d+(\\.\\d+)?$"))) {
                    Toast.makeText(requireContext(), "El salario debe ser un número válido", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        //1-creo un objeto de la clse conexion
                        val objConexion = ClaseConexion().cadenaConexion()

                        //2-creo una variable que contenga un PrepareStatement
                        val addTrabajo =
                            objConexion?.prepareStatement("INSERT INTO TRABAJO ( Titulo , IdEmpleador , AreaDeTrabajo,Descripcion ,Ubicacion , Experiencia , Requerimientos , Estado ,Salario , Beneficios, FechaDePublicacion ) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,? )")!!
                        addTrabajo.setString(1, txtTituloJob.text.toString())
                        addTrabajo.setString(2, idEmpleador)
                        addTrabajo.setString(3, spnTiposTrabajo.selectedItem.toString())
                        addTrabajo.setString(4, txtDescripcionJob.text.toString())
                        addTrabajo.setString(5, txtUbicacionJob.text.toString())
                        addTrabajo.setString(6, txtExperienciaJob.text.toString())
                        addTrabajo.setString(7, txtHabilidadesJob.text.toString())
                        addTrabajo.setString(8, "Activo")

                        val salario = BigDecimal(txtSalarioJob.text.toString())
                        addTrabajo.setBigDecimal(9, salario)

                        addTrabajo.setString(10, txtBeneficiosJob.text.toString())
                        addTrabajo.setString(11, fechaDePublicacion)



                        Log.d(
                            "InsertJob",
                            "Datos a insertar: Titulo=${txtTituloJob.text}, IdEmpleador=$idEmpleador, AreaDeTrabajo=${spnTiposTrabajo.selectedItem}, Descripcion=${txtDescripcionJob.text}, Ubicacion=${txtUbicacionJob.text}, Experiencia=${txtExperienciaJob.text}, Requerimientos=${txtHabilidadesJob.text}, Estado=Activo, Salario=$salario, Beneficios=${txtBeneficiosJob.text}, FechaDePublicacion=$fechaDePublicacion"
                        )

                        addTrabajo.executeUpdate()
                        val TrabajoDb = obtenerDatos()

                        withContext(Dispatchers.Main) {
                            (rcvTrabajos.adapter as? AdaptadorTrabajos)?.actualizarDatos(TrabajoDb)
                            Toast.makeText(requireContext(), "Trabajo Ingresado", Toast.LENGTH_LONG)
                                .show()
                            dialog.dismiss()
                        }
                    } catch (e: Exception) {
                        Log.e("InsertJob", "Error al insertar trabajo", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Error al insertar trabajo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        //aqui estaban antes
                    }


                }

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
