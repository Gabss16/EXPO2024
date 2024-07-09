package com.example.expogbss

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.AreaDeTrabajo
import modelo.ClaseConexion

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
        val btnShowBottomSheet = root.findViewById<Button>(R.id.idBtnShowBottomSheet)


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
            val spnTipoTrabajo = view.findViewById<Spinner>(R.id.spnTiposTrabajo)

            fun obtenerAreasDeTrabajo(): List<AreaDeTrabajo> {
                val listadoDeAreasDeTrabajo = mutableListOf<AreaDeTrabajo>()
                val objConexion = ClaseConexion().cadenaConexion()

                if (objConexion != null) {
                    // Creo un Statement que me ejecutará el select
                    val statement = objConexion.createStatement()
                    val resultSet = statement?.executeQuery("select * from AreaDeTrabajo")

                    if (resultSet != null) {
                        while (resultSet.next()) {
                            val IdArea = resultSet.getInt("IdAreaDeTrabajo")
                            val NombreAreaDetrabajo = resultSet.getString("NombreAreaDetrabajo")
                            val listadoCompleto = AreaDeTrabajo(IdArea, NombreAreaDetrabajo)
                            listadoDeAreasDeTrabajo.add(listadoCompleto)
                        }
                        resultSet.close()
                    }
                    statement?.close()
                    objConexion.close()
                } else {
                    Log.e("registroSolicitante", "Connection to database failed")
                }

                return listadoDeAreasDeTrabajo
            }

            val spAreaDeTrabajoSolicitante = view.findViewById<Spinner>(R.id.spnTiposTrabajo)
            CoroutineScope(Dispatchers.IO).launch {
                val listadoDeAreasDeTrabajo = obtenerAreasDeTrabajo()
                val AreasDeTrabajo = listadoDeAreasDeTrabajo.map { it.NombreAreaDetrabajo }

                withContext(Dispatchers.Main) {
                    // Creo la configuración del adaptador
                    // El Adaptador solicita tres cosas: contexto, layout y los datos
                    val adapter = ArrayAdapter(requireContext(), // Usar el contexto adecuado
                        android.R.layout.simple_spinner_dropdown_item,
                        AreasDeTrabajo
                    )
                    spAreaDeTrabajoSolicitante.adapter = adapter
                }
            }



            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {

                CoroutineScope(Dispatchers.IO).launch {
                    //1-creo un objeto de la clse conexion
                    val objConexion = ClaseConexion().cadenaConexion()

                    //2-creo una variable que contenga un PrepareStatement
                    val addTrabajo =
                        objConexion?.prepareStatement("insert into TRABAJO values (?, ?, ?,?,?,?,?,?,?,?,?")!!
                    addTrabajo.setString(1, txtTituloJob.text.toString())
                    addTrabajo.setString(2, txtUbicacionJob.text.toString())
                    addTrabajo.setString(3, txtDescripcionJob.text.toString())
                    addTrabajo.setString(4, txtExperienciaJob.text.toString())
                    addTrabajo.setString(5, txtHabilidadesJob.text.toString())
                    addTrabajo.setString(6, spnTipoTrabajo.toString())
                    addTrabajo.setString(7, txtBeneficiosJob.text.toString())
                    addTrabajo.setString(8, txtSalarioJob.text.toString())

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
            }}



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
