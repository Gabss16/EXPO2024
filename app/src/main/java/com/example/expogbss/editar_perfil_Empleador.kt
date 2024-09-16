package com.example.expogbss

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Window
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena.fotoEmpleador
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Departamento
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.sql.SQLException
import java.util.UUID

class editar_perfil_Empleador : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil_empleador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Mando a llamar a todos los elementos de la vista
        val txtNombreEmpleador = findViewById<EditText>(R.id.txtNombreEmpleadorEditar)
        val txtCorreoEmpleador = findViewById<EditText>(R.id.txtCorreoEmpleadorEditar)
        val txtTelefonoEmpleador = findViewById<EditText>(R.id.txtTelefonoEmpleadorEditar)
        val txtDireccionEmpleador = findViewById<EditText>(R.id.txtDireccionEmpleadorEditar)
        val txtSitioWebEmpleador = findViewById<EditText>(R.id.txtSitioWebEmpleadorEditar)
        val spDepartamentos = findViewById<Spinner>(R.id.spDepartamentoEditar)
        val nombreEmpresa = login.nombreEmpresa
        val correoEmpleador = login.correoEmpleador
        val nombreEmpleador = login.nombreEmpleador
        val numeroEmpleador = login.numeroEmpleador
        val direccionEmpleador = login.direccionEmpleador
        val sitioWebEmpleador = login.sitioWebEmpleador
        val departamentoEmpleador = login.idDepartamento

        val btnSalir4 = findViewById<ImageButton>(R.id.btnSalir4)

        btnSalir4.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        val idEmpleador = login.IdEmpleador

        // Asigna los datos a los EditText correspondientes
        txtCorreoEmpleador.setText(correoEmpleador)
        txtNombreEmpleador.setText(nombreEmpleador)
        txtTelefonoEmpleador.setText(numeroEmpleador)
        txtDireccionEmpleador.setText(direccionEmpleador)
        txtSitioWebEmpleador.setText(sitioWebEmpleador)

        AlertDialog.Builder(this@editar_perfil_Empleador)
            .setTitle("Atención!!")
            .setMessage("Acá puedes editar la información general de tu perfil, solamente edita los datos que quieras cambiar, lo demás no lo toques.")
            .setPositiveButton("Aceptar", null)
            .show()


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
                    this@editar_perfil_Empleador, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    nombresDepartamentos
                )
                spDepartamentos.adapter = adapter

                // Aquí seleccionamos el departamento basado en el IdDepartamento
                val posicionSeleccionada =
                    listadoDepartamentos.indexOfFirst { it.Id_departamento == departamentoEmpleador }

                // Si el departamento existe en la lista, seleccionarlo
                if (posicionSeleccionada != -1) {
                    spDepartamentos.setSelection(posicionSeleccionada)
                }
            }
        }


        println(idEmpleador)
        val btnEditarPerfilEmpleador = findViewById<ImageView>(R.id.btnEditarPerfilEmpleador)


        btnEditarPerfilEmpleador.setOnClickListener {
            // Deshabilitar el botón para evitar múltiples clicks
            btnEditarPerfilEmpleador.isEnabled = false

            // Obtener los valores de los EditText
            val nombreEmpleador = txtNombreEmpleador.text.toString().trim()
            val CorreoEmpleador = txtCorreoEmpleador.text.toString().trim()
            val TelefoEmpleador = txtTelefonoEmpleador.text.toString().trim()
            val DireccionEmpleador = txtDireccionEmpleador.text.toString().trim()
            val SitioWebEmpleador = txtSitioWebEmpleador.text.toString().trim()

            val VerificarTelefono = Regex("^\\d{4}-\\d{4}\$")
            val verificarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

            // Validaciones de campos vacíos y otros formatos
            if (nombreEmpleador.isEmpty() || CorreoEmpleador.isEmpty() || TelefoEmpleador.isEmpty() || DireccionEmpleador.isEmpty()) {
                Toast.makeText(
                    this@editar_perfil_Empleador,
                    "Por favor, llenar los espacios obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
                btnEditarPerfilEmpleador.isEnabled = true
            } else if (!VerificarTelefono.matches(TelefoEmpleador)) {
                Toast.makeText(
                    this@editar_perfil_Empleador,
                    "Ingresar un número de teléfono válido.",
                    Toast.LENGTH_SHORT
                ).show()
                btnEditarPerfilEmpleador.isEnabled = true
            } else if (!verificarCorreo.matches(CorreoEmpleador)) {
                Toast.makeText(
                    this@editar_perfil_Empleador,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
                btnEditarPerfilEmpleador.isEnabled = true
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Realizar la actualización en la base de datos
                        val objConexion = ClaseConexion().cadenaConexion()

                        // Comprobar si existe algún solicitante con el mismo teléfono
                        val comprobarSiExistetelefonoSolicitante =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE Telefono = ?")!!
                        comprobarSiExistetelefonoSolicitante.setString(1, TelefoEmpleador)

                        val existeTelefonoSolicitante =
                            comprobarSiExistetelefonoSolicitante.executeQuery()

                        if (existeTelefonoSolicitante.next()) {
                            // Si existe un solicitante con el mismo teléfono
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@editar_perfil_Empleador,
                                    "Ya existe alguien con ese número de teléfono, por favor ingresa uno distinto.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnEditarPerfilEmpleador.isEnabled = true
                            }
                        } else {
                            // Comprobar si existe algún otro empleador con el mismo teléfono
                            val comprobarSiExisteTelefonoEmpleador =
                                objConexion?.prepareStatement("SELECT * FROM Empleador WHERE NumeroTelefono = ? AND IdEmpleador != ?")!!
                            comprobarSiExisteTelefonoEmpleador.setString(1, TelefoEmpleador)
                            comprobarSiExisteTelefonoEmpleador.setString(2, idEmpleador)

                            val existeTelefonoEmpleador =
                                comprobarSiExisteTelefonoEmpleador.executeQuery()

                            if (existeTelefonoEmpleador.next()) {
                                // Si existe otro empleador con el mismo teléfono
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@editar_perfil_Empleador,
                                        "Ya existe alguien con ese número de teléfono, por favor, utiliza otro.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnEditarPerfilEmpleador.isEnabled = true
                                }
                            } else {

                                val DepartamentoNombre =
                                    spDepartamentos.selectedItem.toString()

                                // Obtener el id_Departamento desde el Spinner
                                val Departamento =
                                    obtenerDepartamentos() // Se asume que puedes obtener la lista de Departamentos aquí
                                val DepartamentoSeleccionado =
                                    Departamento.find { it.Nombre == DepartamentoNombre }
                                val idDepartamento =
                                    DepartamentoSeleccionado!!.Id_departamento

                                // Actualizar los datos del empleador en la base de datos
                                val actualizarUsuario = objConexion?.prepareStatement(
                                    "UPDATE EMPLEADOR SET CorreoElectronico = ?, NumeroTelefono = ?, Direccion = ?, SitioWeb = ?, NombreRepresentante = ?, IdDepartamento = ? WHERE IdEmpleador = ?"
                                )!!
                                actualizarUsuario.setString(1, CorreoEmpleador)
                                actualizarUsuario.setString(2, TelefoEmpleador)
                                actualizarUsuario.setString(3, DireccionEmpleador)
                                actualizarUsuario.setString(4, SitioWebEmpleador)
                                actualizarUsuario.setString(5, nombreEmpleador)
                                actualizarUsuario.setInt(6, idDepartamento)
                                actualizarUsuario.setString(7, idEmpleador)

                                val filasAfectadas = actualizarUsuario.executeUpdate()

                                if (filasAfectadas > 0) {
                                    // Actualización exitosa, actualizar variables globales
                                    login.correoEmpleador = CorreoEmpleador
                                    login.numeroEmpleador = TelefoEmpleador
                                    login.direccionEmpleador = DireccionEmpleador
                                    login.sitioWebEmpleador = SitioWebEmpleador
                                    login.nombreEmpleador = nombreEmpleador
                                    login.idDepartamento = idDepartamento

                                    withContext(Dispatchers.Main) {
                                        AlertDialog.Builder(this@editar_perfil_Empleador)
                                            .setTitle("Perfil actualizado")
                                            .setMessage("Los datos del perfil han sido actualizados correctamente.")
                                            .setPositiveButton("Aceptar", null)
                                            .show()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@editar_perfil_Empleador,
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
                                        this@editar_perfil_Empleador,
                                        "Ya existe un usuario con ese correo electrónico, por favor ingresa uno distinto.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            else -> {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@editar_perfil_Empleador,
                                        "Error SQL: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@editar_perfil_Empleador,
                                "Ocurrió un error al actualizar el perfil. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")
                        }
                    } finally {
                        // Habilitar el botón nuevamente después de completar la coroutine
                        withContext(Dispatchers.Main) {
                            btnEditarPerfilEmpleador.isEnabled = true
                        }
                    }
                }
            }
        }

    }
}