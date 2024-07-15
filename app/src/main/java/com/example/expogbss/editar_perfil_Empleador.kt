package com.example.expogbss

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
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
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.sql.SQLException
import java.util.UUID

class editar_perfil_Empleador : AppCompatActivity() {
    private val codigo_opcion_galeria = 102
    private val codigo_opcion_tomar_foto = 103
    private val CAMERA_REQUEST_CODE = 0
    private val STORAGE_REQUEST_CODE = 1

    private lateinit var imgFotoDePerfilEmpleador: ImageView
    private var imageUri: String? = null
    private var fotoSubida = false

    private lateinit var miPathEmpresa: String
    private val uuid = UUID.randomUUID().toString()

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
        val txtEmpresaEmpleador = findViewById<EditText>(R.id.txtNombreEmpresaEmpleadorEditar)
        val txtCorreoEmpleador = findViewById<EditText>(R.id.txtCorreoEmpleadorEditar)
        val txtContrasenaEmpleador = findViewById<EditText>(R.id.txtContrasenaEmpleadorEditar)
        val txtTelefonoEmpleador = findViewById<EditText>(R.id.txtTelefonoEmpleadorEditar)
        val txtDireccionEmpleador = findViewById<EditText>(R.id.txtDireccionEmpleadorEditar)
        val txtSitioWebEmpleador = findViewById<EditText>(R.id.txtSitioWebEmpleadorEditar)
        val spDepartamentos = findViewById<Spinner>(R.id.spDepartamentoEditar)
        imgFotoDePerfilEmpleador = findViewById(R.id.imgFotoDePerfilEmpleador)
        val btnSubirFotoEmpleador = findViewById<Button>(R.id.btnSubirFotoEmpleadorEditar)
        val btnTomarFotoEmpleador = findViewById<Button>(R.id.btnTomarFotoEmpleadorEditar)


        val nombreEmpresa = login.nombreEmpresa
        val correoEmpleador = login.correoEmpleador
        val nombreEmpleador = login.nombreEmpleador
        val numeroEmpleador = login.numeroEmpleador
        val direccionEmpleador = login.direccionEmpleador
        val sitioWebEmpleador = login.sitioWebEmpleador

        val idEmpleador = login.IdEmpleador

        println("$idEmpleador")

        // Deshabilitar los EditText de empresa y contraseña
        txtEmpresaEmpleador.isEnabled = false
        txtContrasenaEmpleador.isEnabled = false

        btnTomarFotoEmpleador.isEnabled = false
        btnSubirFotoEmpleador.isEnabled = false

        // Asigna los datos a los EditText correspondientes
        txtEmpresaEmpleador.setText(nombreEmpresa)
        txtCorreoEmpleador.setText(correoEmpleador)
        txtNombreEmpleador.setText(nombreEmpleador)
        txtTelefonoEmpleador.setText(numeroEmpleador)
        txtDireccionEmpleador.setText(direccionEmpleador)
        txtSitioWebEmpleador.setText(sitioWebEmpleador)

        AlertDialog.Builder(this@editar_perfil_Empleador)
            .setTitle("Atención!!")
            .setMessage("En esta versión no puedes editar tu contraseña y foto de perfil, se incluirá más adelante.")
            .setPositiveButton("Aceptar", null)
            .show()

        Glide.with(this).load(login.fotoEmpleador).into(imgFotoDePerfilEmpleador)

        val listadoDepartamentos = listOf(
            "Ahuachapán", "Cabañas", "Chalatenango", "Cuscatlán", "La Libertad", "Morazán",
            "La Paz", "Santa Ana", "San Miguel", "San Vicente", "San Salvador", "Sonsonate",
            "La Unión", "Usulután"
        )
        val adaptadorDeLinea =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoDepartamentos)
        spDepartamentos.adapter = adaptadorDeLinea

        val btnEditarPerfilEmpleador = findViewById<ImageView>(R.id.btnEditarPerfilEmpleador)

        // Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnSubirFotoEmpleador.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido, abrir la galería
                openGallery()
            } else {
                // Permiso no concedido, solicitar permiso
                checkStoragePermission()
            }
        }
        btnTomarFotoEmpleador.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido, abrir la cámara
                openCamera()
            } else {
                // Permiso no concedido, solicitar permiso
                checkCameraPermission()
            }
        }

        btnEditarPerfilEmpleador.setOnClickListener {
            // Deshabilitar el botón para evitar múltiples clicks
            btnEditarPerfilEmpleador.isEnabled = false

            // Obtener los valores de los EditText
            val nombreEmpleador = txtNombreEmpleador.text.toString()
            val CorreoEmpleador = txtCorreoEmpleador.text.toString()
            val ContrasenaEmpleador = txtContrasenaEmpleador.text.toString()
            val TelefoEmpleador = txtTelefonoEmpleador.text.toString()
            val DireccionEmpleador = txtDireccionEmpleador.text.toString()
            val SitioWebEmpleador = txtSitioWebEmpleador.text.toString()
            val EmpresaEmpleador = txtEmpresaEmpleador.text.toString()

            val VerificarTelefono = Regex("^\\d{4}-\\d{4}\$")
            val verificarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val verificarContraseña =
                Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

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
                                // Actualizar los datos del empleador en la base de datos
                                val actualizarUsuario = objConexion?.prepareStatement(
                                    "UPDATE EMPLEADOR SET CorreoElectronico = ?, NumeroTelefono = ?, Direccion = ?, SitioWeb = ?, NombreRepresentante = ?, Departamento = ? WHERE IdEmpleador = ?"
                                )!!
                                actualizarUsuario.setString(1, CorreoEmpleador)
                                actualizarUsuario.setString(2, TelefoEmpleador)
                                actualizarUsuario.setString(3, DireccionEmpleador)
                                actualizarUsuario.setString(4, SitioWebEmpleador)
                                actualizarUsuario.setString(5, nombreEmpleador)
                                actualizarUsuario.setString(
                                    6,
                                    spDepartamentos.selectedItem.toString()
                                )
                                actualizarUsuario.setString(7, idEmpleador)

                                val filasAfectadas = actualizarUsuario.executeUpdate()

                                if (filasAfectadas > 0) {
                                    // Actualización exitosa, actualizar variables globales
                                    login.nombreEmpresa = EmpresaEmpleador
                                    login.correoEmpleador = CorreoEmpleador
                                    login.nombreEmpleador = nombreEmpleador
                                    login.numeroEmpleador = TelefoEmpleador
                                    login.direccionEmpleador = DireccionEmpleador
                                    login.sitioWebEmpleador = SitioWebEmpleador

                                    withContext(Dispatchers.Main) {
                                        AlertDialog.Builder(this@editar_perfil_Empleador)
                                            .setTitle("Perfil actualizado")
                                            .setMessage("Los datos del perfil han sido actualizados correctamente.")
                                            .setPositiveButton("Aceptar", null)
                                            .show()

                                        // Limpiar campos si es necesario
                                        txtNombreEmpleador.text.clear()
                                        txtEmpresaEmpleador.text.clear()
                                        txtCorreoEmpleador.text.clear()
                                        txtContrasenaEmpleador.text.clear()
                                        txtTelefonoEmpleador.text.clear()
                                        txtDireccionEmpleador.text.clear()
                                        txtSitioWebEmpleador.text.clear()
                                        imgFotoDePerfilEmpleador.setImageDrawable(null)
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

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                CAMERA_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
        } else {
            openGallery()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, codigo_opcion_tomar_foto)
    }

    private fun openGallery() {
        val intent =
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        startActivityForResult(intent, codigo_opcion_galeria)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                codigo_opcion_tomar_foto -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    imgFotoDePerfilEmpleador.setImageBitmap(photo)
                    fotoSubida = true
                }

                codigo_opcion_galeria -> {
                    val imageUri = data?.data
                    imgFotoDePerfilEmpleador.setImageURI(imageUri)
                    fotoSubida = true
                }
            }
        }
    }

    private fun subirimagenFirebase(
        bitmap: Bitmap,
        onSuccess: (String) -> Unit
    ) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(
                this@editar_perfil_Empleador,
                "Error al subir la imagen",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }
}