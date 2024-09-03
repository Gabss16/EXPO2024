package com.example.expogbss

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
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
import modelo.Solicitante
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
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

        val btnSalir5 = findViewById<ImageButton>(R.id.btnSalir5)

        btnSalir5.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

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

                // Aquí seleccionamos el areaTrabajo basado en el IdAreaTrabajo
                val posicionSeleccionada =
                    listadoAreaDeTrabajo.indexOfFirst { it.idAreaDeTrabajo == areaDeTrabajoSolicitante }

                // Si el area existe en la lista, seleccionarlo
                if (posicionSeleccionada != -1) {
                    spAreaDeTrabajoSolicitante.setSelection(posicionSeleccionada)
                }
            }
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

                // Aquí seleccionamos el areaTrabajo basado en el IdAreaTrabajo
                val posicionSeleccionada =
                    listadoAreaDeTrabajo.indexOfFirst { it.idAreaDeTrabajo == areaDeTrabajoSolicitante }

                // Si el area existe en la lista, seleccionarlo
                if (posicionSeleccionada != -1) {
                    spAreaDeTrabajoSolicitante.setSelection(posicionSeleccionada)
                }
            }
        }


        val listadoSituacionLaboral = listOf(
            "Empleado", "Desempleado"
        )
        val adaptadorDeEstado =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listadoSituacionLaboral
            )
        spEstadoSolicitante.adapter = adaptadorDeEstado
        //Funcion llamar estado
        //fun obtenerEstadoSolicitante(idSolicitante: String): String? {
            //var estadoSolicitante: String? = null
            //val objConexion = ClaseConexion().cadenaConexion()

            //if (objConexion != null) {
                //val query = "SELECT Estado FROM SOLICITANTE WHERE CorreoElectronico = ?"
                //var preparedStatement: PreparedStatement? = null
                //var resultSet: ResultSet? = null

                //try {
                    //preparedStatement = objConexion.prepareStatement(query)
                    //preparedStatement.setString(1, idSolicitante)
                    //resultSet = preparedStatement.executeQuery()

                    //if (resultSet.next()) {
                        //estadoSolicitante = resultSet.getString("Estado")
                    //}
               // } catch (e: SQLException) {
                 //   println("Error al obtener el estado del solicitante: ${e.message}")
                //} finally {
                  //  resultSet?.close()
                    //preparedStatement?.close()
                    //objConexion.close()
               // }
            //} else {
              //  println("No se pudo establecer la conexión con la base de datos.")
            //}
            //return estadoSolicitante
        //}


        val btnEditarPerfilSolicitante = findViewById<ImageButton>(R.id.btnEditarPerfilSolicitanteEdit)


        btnEditarPerfilSolicitante.setOnClickListener {
            // Deshabilitar el botón para evitar múltiples clicks
            btnEditarPerfilSolicitante.isEnabled = false

            // Obtener los valores de los EditText
            val nombreSolicitante = txtNombreSolicitanteEdit.text.toString().trim()
            val correoSolicitante = txtCorreoSolicitanteEdit.text.toString().trim()
            val telefonoSolicitante = txtTelefonoSolicitanteEdit.text.toString().trim()
            val direccionSolicitante = txtDireccionSolicitanteEdit.text.toString().trim()
            val habilidadesSolicitante = txtHabilidadesSolicitanteEdit.text.toString().trim()

            val VerificarTelefono = Regex("^\\d{4}-\\d{4}\$")
            val verificarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

            // Validaciones de campos vacíos y otros formatos
            if (nombreSolicitante.isEmpty() || correoSolicitante.isEmpty() || telefonoSolicitante.isEmpty() || direccionSolicitante.isEmpty() || habilidadesSolicitante.isEmpty()) {
                Toast.makeText(
                    this@editar_perfil_solicitante,
                    "Por favor, llenar los espacios obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
                btnEditarPerfilSolicitante.isEnabled = true
            } else if (!VerificarTelefono.matches(telefonoSolicitante)) {
                Toast.makeText(
                    this@editar_perfil_solicitante,
                    "Ingresar un número de teléfono válido.",
                    Toast.LENGTH_SHORT
                ).show()
                btnEditarPerfilSolicitante.isEnabled = true
            } else if (!verificarCorreo.matches(correoSolicitante)) {
                Toast.makeText(
                    this@editar_perfil_solicitante,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
                btnEditarPerfilSolicitante.isEnabled = true
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Realizar la actualización en la base de datos
                        val objConexion = ClaseConexion().cadenaConexion()

                        // Comprobar si existe algún empleador con el mismo teléfono
                        val comprobarSiExistetelefonoEmpleador =
                            objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE NumeroTelefono = ?")!!
                        comprobarSiExistetelefonoEmpleador.setString(1,telefonoSolicitante)

                        val existeTelefonoEmpleador =
                            comprobarSiExistetelefonoEmpleador.executeQuery()

                        if (existeTelefonoEmpleador.next()) {
                            // Si existe un solicitante con el mismo teléfono
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@editar_perfil_solicitante,
                                    "Ya existe alguien con ese número de teléfono, por favor ingresa uno distinto.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnEditarPerfilSolicitante.isEnabled = true
                            }
                        } else {
                            // Comprobar si existe algún otro solicitante con el mismo teléfono
                            val comprobarSiExisteTelefonoSolicitante =
                                objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE Telefono = ? AND IdSolicitante != ?")!!
                            comprobarSiExisteTelefonoSolicitante.setString(1, telefonoSolicitante)
                            comprobarSiExisteTelefonoSolicitante.setString(2, idSolicitante)

                            val existeTelefonoSolicitante =
                                comprobarSiExisteTelefonoSolicitante.executeQuery()

                            if (existeTelefonoEmpleador.next()) {
                                // Si existe otro solicitante con el mismo teléfono
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@editar_perfil_solicitante,
                                        "Ya existe alguien con ese número de teléfono, por favor, utiliza otro.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnEditarPerfilSolicitante.isEnabled = true
                                }
                            } else {

                                val DepartamentoNombre =
                                    spDepartamentoSolicitanteEdit.selectedItem.toString()

                                // Obtener el id_Departamento desde el Spinner
                                val Departamento =
                                    obtenerDepartamentos() // Se asume que puedes obtener la lista de Departamentos aquí
                                val DepartamentoSeleccionado =
                                    Departamento.find { it.Nombre == DepartamentoNombre }
                                val idDepartamento =
                                    DepartamentoSeleccionado!!.Id_departamento

                                val AreaNombre =
                                    spAreaDeTrabajoSolicitante.selectedItem.toString()

                                // Obtener el id_areaTrabajo desde el Spinner
                                val AreaDeTrabajo =
                                    obtenerArea() // Se asume que puedes obtener la lista de Areas aquí
                                val AreaSeleccionada =
                                    AreaDeTrabajo.find { it.NombreAreaDetrabajo == AreaNombre }
                                val idAreaTrabajo =
                                    AreaSeleccionada!!.idAreaDeTrabajo

                                // Actualizar los datos del empleador en la base de datos
                                val actualizarUsuario = objConexion?.prepareStatement(
                                    "UPDATE SOLICITANTE SET Nombre  = ?, CorreoElectronico  = ?, Telefono  = ?, Direccion  = ?, IdDepartamento  = ?, IdAreaDeTrabajo = ?, Habilidades  =?, Estado =?  WHERE IdEmpleador = ?"
                                )!!
                                actualizarUsuario.setString(1, nombreSolicitante)
                                actualizarUsuario.setString(2, correoSolicitante)
                                actualizarUsuario.setString(3, telefonoSolicitante)
                                actualizarUsuario.setString(4, direccionSolicitante)
                                actualizarUsuario.setInt(5, idDepartamento)
                                actualizarUsuario.setInt(6, idAreaTrabajo)
                                actualizarUsuario.setString(7, habilidadesSolicitante)
                                actualizarUsuario.setString(8,spEstadoSolicitante.selectedItem.toString())

                                val filasAfectadas = actualizarUsuario.executeUpdate()

                                if (filasAfectadas > 0) {
                                    // Actualización exitosa, actualizar variables globales
                                    login.nombresSolicitante = nombreSolicitante
                                    login.correoSolicitante = correoSolicitante
                                    login.numeroSolicitante = telefonoSolicitante
                                    login.direccionSolicitante = direccionSolicitante
                                    login.idDepartamento = idDepartamento
                                    login.areaDeTrabajo = idAreaTrabajo
                                    login.habilidades = habilidadesSolicitante

                                    withContext(Dispatchers.Main) {
                                        AlertDialog.Builder(this@editar_perfil_solicitante)
                                            .setTitle("Perfil actualizado")
                                            .setMessage("Los datos del perfil han sido actualizados correctamente.")
                                            .setPositiveButton("Aceptar", null)
                                            .show()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@editar_perfil_solicitante,
                                            "Error al actualizar el perfil.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    } catch (e: SQLException) {
                        when (e.errorCode) {
                            1 -> { // ORA-00001: unique constraint violated
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@editar_perfil_solicitante,
                                        "Ya existe un usuario con ese correo electrónico, por favor ingresa uno distinto.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            else -> {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@editar_perfil_solicitante,
                                        "Error SQL: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@editar_perfil_solicitante,
                                "Ocurrió un error al actualizar el perfil. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")
                        }
                    } finally {
                        // Habilitar el botón nuevamente después de completar la coroutine
                        withContext(Dispatchers.Main) {
                            btnEditarPerfilSolicitante.isEnabled = true
                        }
                    }
                }
            }
        }
    }
}