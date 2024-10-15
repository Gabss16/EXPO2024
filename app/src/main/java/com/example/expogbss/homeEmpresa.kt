package com.example.expogbss

import RecicleViewHelpers.AdaptadorPublicacion
import android.content.Intent
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
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.AreaDeTrabajo
import modelo.ClaseConexion
import modelo.Departamento
import modelo.Trabajo
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private var latitudTrabajo: Double? = null
    private var longitudTrabajo: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home_empresa, container, false)

        // initializing our variable for button with its id.
        val btnShowBottomSheet = root.findViewById<ImageButton>(R.id.idBtnShowBottomSheet)
        val btnReactivarTrabajos = root.findViewById<ImageView>(R.id.btnHistorialTrabajo)
        val rcvTrabajos = root.findViewById<RecyclerView>(R.id.rcvTrabajos)
        val idEmpleador = login.IdEmpleador
        println("este es el id empleador $idEmpleador")

        rcvTrabajos.layoutManager = LinearLayoutManager(requireContext())

        println(idEmpleador)

        fun obtenerDatos(): List<Trabajo> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()


            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.prepareStatement("""SELECT 
    T.IdTrabajo, 
    T.Titulo, 
    T.IdEmpleador, 
    A.NombreAreaDetrabajo AS NombreAreaDeTrabajo, 
    T.Descripcion,   
    T.Direccion, 
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
INNER JOIN 
    AreaDeTrabajo A
ON 
    T.IdAreaDeTrabajo = A.IdAreaDeTrabajo
 WHERE IdEmpleador = ?  AND Estado = 'Activo'""")

            statement?.setString(1, idEmpleador)
            val resultSet = statement?.executeQuery()!!

            //en esta variable se añaden TODOS los valores de mascotas
            val listaTrabajos = mutableListOf<Trabajo>()



            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val NombreAreaDeTrabajo  = resultSet.getString("NombreAreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Ubicacion = resultSet.getString("Direccion")
                val Longitud = resultSet.getDouble("Longitud")
                val Latitud = resultSet.getDouble("Longitud")
                val Departamento = resultSet.getInt("IdDepartamento")
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
                    idEmpleador,
                    NombreAreaDeTrabajo,
                    Descripcion,
                    Ubicacion,
                    Longitud,
                    Latitud,
                    Departamento,
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
                val adapter = AdaptadorPublicacion(TrabajoDb)
                rcvTrabajos.adapter = adapter
            }
        }

        btnReactivarTrabajos.setOnClickListener {

            // Iniciar la actividad "editar_perfil_Empleador"
            val intent = Intent(activity, trabajos_Inactivos::class.java)
            startActivity(intent)

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
            val txtSalarioJobMinimo = view.findViewById<EditText>(R.id.txtSalarioJobMinimo)
            val txtSalarioJobMaximo = view.findViewById<EditText>(R.id.txtSalarioJobMaximo)

            txtUbicacionJob.setOnClickListener {
                val bottomSheet = SelectLocationBottomSheet { direccion, latitud, longitud ->
                    // Mostrar la dirección en el campo de texto
                    txtUbicacionJob.setText(direccion)

                    // Guardar latitud y longitud en variables globales para usar en la inserción
                    latitudTrabajo = latitud
                    longitudTrabajo = longitud
                }
                bottomSheet.show(parentFragmentManager, "SelectLocationBottomSheet")
            }


            val fechaDePublicacion =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            //spinerTrabajo
            val spAreaDeTrabajoSolicitante = view.findViewById<Spinner>(R.id.spnTiposTrabajo)

            fun obtenerAreasDeTrabajo(): List<AreaDeTrabajo> {
                val listadoAreaDeTrabajo = mutableListOf<AreaDeTrabajo>()
                val objConexion = ClaseConexion().cadenaConexion()

                if (objConexion != null) {
                    // Creo un Statement que me ejecutará el select
                    val statement = objConexion.createStatement()
                    val resultSet = statement?.executeQuery("select * from AreaDeTrabajo")

                    if (resultSet != null) {
                        while (resultSet.next()) {
                            val idAreaDeTrabajo = resultSet.getInt("IdAreaDeTrabajo")
                            val NombreAreaDetrabajo = resultSet.getString("NombreAreaDetrabajo")
                            val listadoCompleto = AreaDeTrabajo(idAreaDeTrabajo, NombreAreaDetrabajo)
                            listadoAreaDeTrabajo.add(listadoCompleto)
                        }
                        resultSet.close()
                    }
                    statement?.close()
                    objConexion.close()
                } else {
                    Log.e("registroSolicitante", "Connection to database failed")
                }
                return listadoAreaDeTrabajo
            }
            CoroutineScope(Dispatchers.IO).launch {
                val listadoAreaDeTrabajo = obtenerAreasDeTrabajo()
                val AreaDeTrabajo = listadoAreaDeTrabajo.map { it.NombreAreaDetrabajo }

                withContext(Dispatchers.Main) {
                    // Configuración del adaptador
                    val adapter = ArrayAdapter(
                        requireContext(), // Usar el contexto adecuado
                        android.R.layout.simple_spinner_dropdown_item,
                        AreaDeTrabajo
                    )
                    spAreaDeTrabajoSolicitante.adapter = adapter
                }
            }

            //spinnerDepartamentos
            val spDepartamentoSolicitante = view.findViewById<Spinner>(R.id.spnDepartamentos)

            fun obtenerDepartamentos(): List<Departamento> {
                val listadoDepartamento = mutableListOf<Departamento>()
                val objConexion = ClaseConexion().cadenaConexion()

                if (objConexion != null) {
                    // Creo un Statement que me ejecutará el select
                    val statement = objConexion.createStatement()
                    val resultSet = statement?.executeQuery("select * from DEPARTAMENTO")

                    if (resultSet != null) {
                        while (resultSet.next()) {
                            val idDepartamento = resultSet.getInt("idDepartamento")
                            val Nombre = resultSet.getString("Nombre")
                            val listadoCompleto = Departamento(idDepartamento, Nombre)
                            listadoDepartamento.add(listadoCompleto)
                        }
                        resultSet.close()
                    }
                    statement?.close()
                    objConexion.close()
                } else {
                    Log.e("registroSolicitante", "Connection to database failed")
                }
                return listadoDepartamento
            }
            CoroutineScope(Dispatchers.IO).launch {
                val listadoDepartamentos = obtenerDepartamentos()
                val Departamento = listadoDepartamentos.map { it.Nombre }

                withContext(Dispatchers.Main) {
                    // Configuración del adaptador
                    val adapter = ArrayAdapter(
                        requireContext(), // Usar el contexto adecuado
                        android.R.layout.simple_spinner_dropdown_item,
                        Departamento
                    )
                    spDepartamentoSolicitante.adapter = adapter
                }
            }

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
                    txtSalarioJobMaximo.text.isEmpty() || txtSalarioJobMinimo.text.isEmpty()) {

                    Toast.makeText(requireContext(), "Todos los campos deben estar llenos", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val salarioMinText = txtSalarioJobMinimo.text.toString()
                val salarioMaxText = txtSalarioJobMaximo.text.toString()

                // Regex para validar números con hasta dos decimales y sin comas
                val salarioRegex = Regex("^\\d+(\\.\\d{1,2})?$")

                // Verificar si el texto contiene comas
                if (salarioMinText.contains(",") || salarioMaxText.contains(",")) {
                    Toast.makeText(requireContext(), "El salario no puede contener comas. Ejemplo: use 25000 en lugar de 25,000", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // Validar que los salarios coincidan con el formato sin comas y con hasta dos decimales
                if (!salarioMinText.matches(salarioRegex) || !salarioMaxText.matches(salarioRegex)) {
                    Toast.makeText(requireContext(), "El salario debe ser un número válido con hasta 2 decimales", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // Convertir a BigDecimal
                val salarioMax = BigDecimal(salarioMaxText)
                val salarioMin = BigDecimal(salarioMinText)

                // Verificar si el salario máximo excede 25,000
                val maxPermitido = BigDecimal("25000.00")
                if (salarioMax > maxPermitido || salarioMin > maxPermitido) {
                    Toast.makeText(requireContext(), "El salario no puede exceder 25,000", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // Verificar si el salario mínimo es mayor al salario máximo
                if (salarioMin > salarioMax) {
                    Toast.makeText(requireContext(), "El salario mínimo no puede ser mayor al salario máximo", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }




                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val DepartamentoNombre =
                            spDepartamentoSolicitante.selectedItem.toString()
                        // Obtener el id_medicamento desde el Spinner
                        val Departamento =
                            obtenerDepartamentos() // Se asume que puedes obtener la lista de medicamentos aquí
                        val DepartamentoSeleccionado =
                            Departamento.find { it.Nombre == DepartamentoNombre }
                        val idDepartamento = DepartamentoSeleccionado!!.Id_departamento


                        val AreadetrabajoNombre =
                            spAreaDeTrabajoSolicitante.selectedItem.toString()
                        // Obtener el id_medicamento desde el Spinner
                        val AreaDeTrabajo =
                            obtenerAreasDeTrabajo() // Se asume que puedes obtener la lista de medicamentos aquí
                        val AreaDeTrabajoSeleccionada =
                            AreaDeTrabajo.find { it.NombreAreaDetrabajo == AreadetrabajoNombre }
                        val idAreaDeTrabajo = AreaDeTrabajoSeleccionada!!.idAreaDeTrabajo

                        //1-creo un objeto de la clse conexion
                        val objConexion = ClaseConexion().cadenaConexion()

                        //2-creo una variable que contenga un PrepareStatement

                        val addTrabajo =
                            objConexion?.prepareStatement("INSERT INTO TRABAJO ( Titulo , IdEmpleador , IdAreaDeTrabajo,Descripcion ,Direccion , Longitud, Latitud, IdDepartamento, Experiencia , Requerimientos , Estado ,SalarioMinimo, SalarioMaximo , Beneficios, FechaDePublicacion ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,? )")!!
                        addTrabajo.setString(1, txtTituloJob.text.toString().trim())
                        addTrabajo.setString(2, idEmpleador)
                        addTrabajo.setInt(3, idAreaDeTrabajo)
                        addTrabajo.setString(4, txtDescripcionJob.text.toString().trim())
                        addTrabajo.setString(5, txtUbicacionJob.text.toString().trim())
                        addTrabajo.setDouble(6, latitudTrabajo ?: 0.0) // Latitud
                        addTrabajo.setDouble(7, longitudTrabajo ?: 0.0) // Longitud
                        addTrabajo.setInt(8, idDepartamento)
                        addTrabajo.setString(9, txtExperienciaJob.text.toString().trim())
                        addTrabajo.setString(10, txtHabilidadesJob.text.toString().trim())
                        addTrabajo.setString(11, "Activo")
                        addTrabajo.setBigDecimal(12, salarioMin)
                        addTrabajo.setBigDecimal(13, salarioMax)
                        addTrabajo.setString(14, txtBeneficiosJob.text.toString().trim())
                        addTrabajo.setString(15, fechaDePublicacion)

                        Log.d(
                            "InsertJob",
                            "Datos a insertar: Titulo=${txtTituloJob.text}, IdEmpleador=$idEmpleador, IdAreaDeTrabajo=$idAreaDeTrabajo, Descripcion=${txtDescripcionJob.text}, Direccion=${txtUbicacionJob.text},idDepartamento=$idDepartamento ,Experiencia=${txtExperienciaJob.text}, Requerimientos=${txtHabilidadesJob.text}, Estado=Activo, SalarioMinimo=$salarioMin, SalarioMaximo=$salarioMax Beneficios=${txtBeneficiosJob.text}, FechaDePublicacion=$fechaDePublicacion"
                        )

                        addTrabajo.executeUpdate()
                        val TrabajoDb = obtenerDatos()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Trabajo Ingresado", Toast.LENGTH_LONG)
                                .show()
                            dialog.dismiss()
                            (rcvTrabajos.adapter as? AdaptadorPublicacion)?.actualizarDatos(TrabajoDb)
                            rcvTrabajos.adapter?.notifyDataSetChanged()
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
                    }


                }

            }
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialog.setCancelable(true)

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
