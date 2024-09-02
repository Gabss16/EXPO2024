package com.example.expogbss

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.AreaDeTrabajo
import modelo.ClaseConexion
import modelo.Departamento
import java.util.UUID

class editar_perfil_solicitante : AppCompatActivity() {
    private val codigo_opcion_galeria = 102
    private val codigo_opcion_tomar_foto = 103
    private val CAMERA_REQUEST_CODE = 0
    private val STORAGE_REQUEST_CODE = 1

    private lateinit var imgFotoDePerfilSolicitante: ImageView
    private var imageUri: String? = null
    private var fotoSubida = false

    private lateinit var miPathSolicitante: String
    private val uuid = UUID.randomUUID().toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil_solicitante)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Mando a llamar a todos los elementos de la vista
        val txtNombreSolicitanteEdit = findViewById<EditText>(R.id.txtNombreSolicitanteEdit)
        val txtCorreoSolicitanteEdit = findViewById<EditText>(R.id.txtCorreoSolicitanteEdit)
        val txtTelefonoSolicitanteEdit = findViewById<EditText>(R.id.txtTelefonoSolicitanteEdit)
        val txtDireccionSolicitanteEdit = findViewById<EditText>(R.id.txtDireccionSolicitanteEdit)
        val txtHabilidadesSolicitanteEdit = findViewById<EditText>(R.id.txtHabilidadesLaboralesEdit)
        val spDepartamentoSolicitanteEdit = findViewById<Spinner>(R.id.spDepartamentoSolicitanteEdit)
        val spEstadoSolicitante = findViewById<Spinner>(R.id.spSituacionLaboralSolicitanteEdit)
        val spAreaDeTrabajoSolicitante = findViewById<Spinner>(R.id.spAreaDeTrabajoSolicitanteEdit)
        val nombreSolicitante = login.nombresSolicitante
        val correoSolicitante = login.correoSolicitante
        val telefonoSolicitante = login.numeroSolicitante
        val direccionSolicitante = login.direccionSolicitante
        val habilidadesSolicitante = login.habilidades
        val departamentoSolicitante = login.idDepartamento
        val estadoSolicitante = login.estadoSolicitante
        val areaDeTrabajoSolicitante = login.areaDeTrabajo


        val idSolicitante = login.IdSolicitante

        //Mando una alerta para el usuario sobre como editar su perfil

        AlertDialog.Builder(this@editar_perfil_solicitante)
            .setTitle("Atención!!")
            .setMessage("Acá puedes editar la información general de tu perfil, solamente edita los datos que quieras cambiar, lo demás no lo toques.")
            .setPositiveButton("Aceptar", null)
            .show()

        // Asigna los datos a los EditText correspondientes
        txtCorreoSolicitanteEdit.setText(correoSolicitante)
        txtNombreSolicitanteEdit.setText(nombreSolicitante)
        txtTelefonoSolicitanteEdit.setText(telefonoSolicitante)
        txtDireccionSolicitanteEdit.setText(direccionSolicitante)
        txtHabilidadesSolicitanteEdit.setText(habilidadesSolicitante)

        // Función para hacer el select de los Departamentos
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
                Log.e("registro_empresa", "Connection to database failed")
            }
            return listadoDepartamento
        }

        CoroutineScope(Dispatchers.IO).launch {
            val listadoDepartamentos = obtenerDepartamentos()
            val nombresDepartamentos = listadoDepartamentos.map { it.Nombre }

            withContext(Dispatchers.Main) {
                // Configuración del adaptador
                val adapter = ArrayAdapter(
                    this@editar_perfil_solicitante, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    nombresDepartamentos
                )
                spDepartamentoSolicitanteEdit.adapter = adapter

                // Aquí seleccionamos el departamento basado en el IdDepartamento
                val posicionSeleccionada =
                    listadoDepartamentos.indexOfFirst { it.Id_departamento == departamentoSolicitante }

                // Si el departamento existe en la lista, seleccionarlo
                if (posicionSeleccionada != -1) {
                    spDepartamentoSolicitanteEdit.setSelection(posicionSeleccionada)
                }
            }
        }

        // Función para hacer el select del area de trabajo
        fun obtenerArea(): List<AreaDeTrabajo> {
            val listadoAreaDeTrabajo = mutableListOf<AreaDeTrabajo>()
            val objConexion = ClaseConexion().cadenaConexion()

            if (objConexion != null) {
                // Creo un Statement que me ejecutará el select
                val statement = objConexion.createStatement()
                val resultSet = statement?.executeQuery("select * from AreaDeTrabajo")

                if (resultSet != null) {
                    while (resultSet.next()) {
                        val IdAreaDeTrabajo = resultSet.getInt("IdAreaDeTrabajo")
                        val NombreAreaDetrabajo = resultSet.getString("NombreAreaDetrabajo")
                        val listadoCompleto = AreaDeTrabajo(IdAreaDeTrabajo, NombreAreaDetrabajo)
                        listadoAreaDeTrabajo.add(listadoCompleto)
                    }
                    resultSet.close()
                }
                statement?.close()
                objConexion.close()
            } else {
                Log.e("registro_empresa", "Connection to database failed")
            }
            return listadoAreaDeTrabajo
        }

        CoroutineScope(Dispatchers.IO).launch {
            val listadoAreaDeTrabajo = obtenerArea()
            val NombreAreaDetrabajo = listadoAreaDeTrabajo.map { it.NombreAreaDetrabajo }

            withContext(Dispatchers.Main) {
                // Configuración del adaptador
                val adapter = ArrayAdapter(
                    this@editar_perfil_solicitante, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    NombreAreaDetrabajo
                )
                spAreaDeTrabajoSolicitante.adapter = adapter

                // Aquí seleccionamos el departamento basado en el IdDepartamento
                val posicionSeleccionada =
                    listadoAreaDeTrabajo.indexOfFirst { it.idAreaDeTrabajo == areaDeTrabajoSolicitante }

                // Si el departamento existe en la lista, seleccionarlo
                if (posicionSeleccionada != -1) {
                    spDepartamentoSolicitanteEdit.setSelection(posicionSeleccionada)
                }
            }
        }
    }
}