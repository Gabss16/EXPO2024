package com.example.expogbss

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.*
import android.net.Uri
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.example.expogbss.ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.codigo
import com.example.expogbss.ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.correoIngresado
import com.google.firebase.database.database
import modelo.Departamento
import java.sql.SQLException


class registro_empresa : AppCompatActivity() {
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
        setContentView(R.layout.activity_registro_empresa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun generarHTMLCorreo(): String {
            return """
<html>
<body style="font-family: 'Roboto', sans-serif;
            background-color: #f5f7fa;
            margin: 0;
            padding: 0;">
    <div class="container" style="width: 100%;
            max-width: 600px; 
            margin: 50px auto;
            background-color: #ffffff;
            padding: 30px 20px;
            border-radius: 15px;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);">
        <div class="img" style="text-align: center;
            margin-top: 40px;">
            <img src="https://i.imgur.com/bXHJUmC.png" alt="Logo" width="400" style="border-radius: 10px;">
        </div>
        <div class="message" style="text-align: center;
            color: #2c3e50;
            margin-bottom: 40px;">
            <h2 style="font-size: 28px; 
            font-weight: 600;
            margin-bottom: 10px;">Creación de cuenta</h2>
            <p style="font-size: 18px; color: #7f8c8d;">
                Su cuenta ha sido creada. Sin embargo, no podrá utilizar su cuenta hasta nuevo aviso. Primero, debemos asegurarnos de la autenticidad de sus datos, ya que se ha registrado en nombre de una empresa. Le informaremos tan pronto como la verificación se haya completado.
            </p>
        </div>
        <div class="footer-logo" style="text-align: center;
            margin-top: 40px;">
            <img src="https://i.imgur.com/TU8KAcy.png" alt="Logo" width="550" style="border-radius: 10px;">
        </div>
    </div>
</body>
</html>
""".trimIndent()
        }

        // Mando a llamar a todos los elementos de la vista
        val txtNombreEmpleador = findViewById<EditText>(R.id.txtNombreEmpleador)
        val txtEmpresaEmpleador = findViewById<EditText>(R.id.txtNombreEmpresaEmpleador)
        val txtCorreoEmpleador = findViewById<EditText>(R.id.txtCorreoEmpleador)
        val txtContrasenaEmpleador = findViewById<EditText>(R.id.txtContrasenaEmpleador)
        val txtTelefonoEmpleador = findViewById<EditText>(R.id.txtTelefonoEmpleador)
        val txtDireccionEmpleador = findViewById<EditText>(R.id.txtDireccionEmpleador)
        val txtSitioWebEmpleador = findViewById<EditText>(R.id.txtSitioWebEmpleador)
        val spDepartamentos = findViewById<Spinner>(R.id.spDepartamento)
        imgFotoDePerfilEmpleador = findViewById(R.id.imgFotoDePerfilEmpleador)
        val btnSubirFotoEmpleador = findViewById<Button>(R.id.btnSubirFotoEmpleador)
        val btnTomarFotoEmpleador = findViewById<Button>(R.id.btnTomarFotoEmpleador)

        val btnSalir7 = findViewById<ImageButton>(R.id.btnSalir7)

        btnSalir7.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        var isPasswordVisible = false
        val passwordview = txtContrasenaEmpleador
        val togglePasswordVisibility = findViewById<ImageView>(R.id.mostrarContraRegistEmpl)

        togglePasswordVisibility.setOnClickListener {
            if (isPasswordVisible) {
                // Ocultar contraseña
                passwordview.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.nuevacontra)
            } else {
                // Mostrar contraseña
                passwordview.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.mostrarcontrasena)
            }
            isPasswordVisible = !isPasswordVisible
            passwordview.setSelection(passwordview.text.length)
        }

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
            val Departamento = listadoDepartamentos.map { it.Nombre }

            withContext(Dispatchers.Main) {
                // Configuración del adaptador
                val adapter = ArrayAdapter(
                    this@registro_empresa, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    Departamento
                )
                spDepartamentos.adapter = adapter
            }
        }

        val btnCrearCuentaEmpleador = findViewById<ImageView>(R.id.btnCrearCuentaEmpleador)

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
                checkStoragePermission()
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
                checkCameraPermission()
            } else {
                // Permiso no concedido, solicitar permiso
                checkCameraPermission()
            }
        }

        btnCrearCuentaEmpleador.setOnClickListener {
            // Deshabilitar el botón para evitar múltiples clicks
            btnCrearCuentaEmpleador.isEnabled = false

            // Mando a llamar a cada textview
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

            // Validaciones de campos vacíos y cosas por ese estilo
            if (nombreEmpleador.isEmpty() || CorreoEmpleador.isEmpty() || ContrasenaEmpleador.isEmpty() || TelefoEmpleador.isEmpty() || DireccionEmpleador.isEmpty()) {
                Toast.makeText(
                    this@registro_empresa,
                    "Por favor, llenar los espacios obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
                btnCrearCuentaEmpleador.isEnabled = true
            } else if (!VerificarTelefono.matches(TelefoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un número de teléfono válido.",
                    Toast.LENGTH_SHORT
                ).show()
                btnCrearCuentaEmpleador.isEnabled = true
            } else if (!verificarCorreo.matches(CorreoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
                btnCrearCuentaEmpleador.isEnabled = true
            } else if (!verificarContraseña.matches(ContrasenaEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "La contraseña debe contener al menos un caracter especial y tener más de 6 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
                btnCrearCuentaEmpleador.isEnabled = true
            } else if (!fotoSubida) {
                Toast.makeText(
                    this@registro_empresa,
                    "Por favor, sube una foto de perfil.",
                    Toast.LENGTH_SHORT
                ).show()
                btnCrearCuentaEmpleador.isEnabled = true
            } else if (EmpresaEmpleador.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val comprobarSiExisteCorreoEmpleador =
                            objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ? ")!!
                        comprobarSiExisteCorreoEmpleador.setString(1, CorreoEmpleador)

                        val comprobarSiExisteCorreoSolicitante =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ? ")!!
                        comprobarSiExisteCorreoSolicitante.setString(1, CorreoEmpleador)

                        val comprobarSiExistetelefonoSolicitante =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE Telefono = ? ")!!
                        comprobarSiExistetelefonoSolicitante.setString(1, TelefoEmpleador)

                        val comprobarSiExistetelefonoEmpleador =
                            objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE NumeroTelefono = ? ")!!
                        comprobarSiExistetelefonoEmpleador.setString(1, TelefoEmpleador)

                        val existeCorreoSolicitante =
                            comprobarSiExisteCorreoSolicitante.executeQuery()
                        val existeCorreoEmpleador = comprobarSiExisteCorreoEmpleador.executeQuery()

                        val existeTelefonoSolicitante =
                            comprobarSiExistetelefonoSolicitante.executeQuery()

                        val existeTelefonoEmpleador =
                            comprobarSiExistetelefonoEmpleador.executeQuery()

                        if (existeCorreoSolicitante.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese correo electrónico, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else if (existeCorreoEmpleador.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese correo electrónico, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else if (existeTelefonoSolicitante.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese número de teléfono, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else if (existeTelefonoEmpleador.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese número de teléfono, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else {
                            // Encripto la contraseña usando la función de encriptación
                            val contrasenaEncriptada =
                                hashSHA256(txtContrasenaEmpleador.text.toString())
                            withContext(Dispatchers.Main) {
                                val bitmap =
                                    (imgFotoDePerfilEmpleador.drawable as BitmapDrawable).bitmap
                                subirimagenFirebase(bitmap) { imageUrl ->
                                    CoroutineScope(Dispatchers.IO).launch {

                                        val DepartamentoNombre =
                                            spDepartamentos.selectedItem.toString()

                                        // Obtener el id_medicamento desde el Spinner
                                        val Departamento =
                                            obtenerDepartamentos() // Se asume que puedes obtener la lista de medicamentos aquí
                                        val DepartamentoSeleccionado =
                                            Departamento.find { it.Nombre == DepartamentoNombre }
                                        val idDepartamento =
                                            DepartamentoSeleccionado!!.Id_departamento


                                        val crearUsuario = objConexion?.prepareStatement(
                                            "INSERT INTO EMPLEADOR (IdEmpleador, NombreEmpresa, CorreoElectronico, NumeroTelefono, Direccion, SitioWeb, NombreRepresentante, IdDepartamento, Contrasena, Estado, Foto) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )"
                                        )!!
                                        crearUsuario.setString(1, uuid)
                                        crearUsuario.setString(
                                            2,
                                            txtEmpresaEmpleador.text.toString()
                                        )
                                        crearUsuario.setString(
                                            3,
                                            txtCorreoEmpleador.text.toString()
                                        )
                                        crearUsuario.setString(
                                            4,
                                            TelefoEmpleador
                                        )
                                        crearUsuario.setString(
                                            5,
                                            txtDireccionEmpleador.text.toString()
                                        )
                                        crearUsuario.setString(6, SitioWebEmpleador)
                                        crearUsuario.setString(
                                            7,
                                            txtNombreEmpleador.text.toString()
                                        )
                                        crearUsuario.setInt(
                                            8,
                                            idDepartamento
                                        )
                                        crearUsuario.setString(9, contrasenaEncriptada)
                                        crearUsuario.setString(10, "Pendiente")
                                        crearUsuario.setString(11, imageUrl)

                                        val filasAfectadas = crearUsuario.executeUpdate()

                                        if (filasAfectadas > 0) {
                                            // La inserción fue exitosa
                                            withContext(Dispatchers.Main) {
                                                val correoEnviado = recuperarContrasena(
                                                    CorreoEmpleador,
                                                    "Creación de cuenta", generarHTMLCorreo()
                                                )

                                                if (correoEnviado) {
                                                    //TODO: ESTA PARTE ES PARA QUE EL NOMBRE Y CONTRASEÑA SE ENVIEN A FIREBASE

                                                    withContext(Dispatchers.Main) {

                                                        FirebaseAuth.getInstance()
                                                            .createUserWithEmailAndPassword(
                                                                CorreoEmpleador,
                                                                ContrasenaEmpleador
                                                            )
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    // Usuario creado en Firebase Authentication
                                                                    val user =
                                                                        FirebaseAuth.getInstance().currentUser
                                                                    val profileUpdates =
                                                                        UserProfileChangeRequest.Builder()
                                                                            .setDisplayName(
                                                                                nombreEmpleador
                                                                            )  // Establecer el nombre del usuario
                                                                            .build()

                                                                    // Actualizar el perfil del usuario con el nombre
                                                                    user?.updateProfile(
                                                                        profileUpdates
                                                                    )
                                                                        ?.addOnCompleteListener { updateTask ->
                                                                            if (updateTask.isSuccessful) {
                                                                                // Obtener el UID del usuario recién creado
                                                                                val userId =
                                                                                    user.uid

                                                                                // Crear referencia a la base de datos de Firebase
                                                                                val databaseRef =
                                                                                    com.google.firebase.Firebase.database.reference

                                                                                // Datos que deseas guardar en la base de datos
                                                                                val userMap = mapOf(
                                                                                    "nombre" to nombreEmpleador,
                                                                                    "correo" to CorreoEmpleador,
                                                                                    "rol" to "empleador" // Puedes agregar un campo 'rol' si es necesario
                                                                                )

                                                                                // Guardar los datos del usuario en la Realtime Database bajo 'solicitantes/{userId}'
                                                                                databaseRef.child("Usuarios")
                                                                                    .child(userId)
                                                                                    .setValue(
                                                                                        userMap
                                                                                    )
                                                                                    .addOnCompleteListener { dbTask ->
                                                                                        if (dbTask.isSuccessful) {
                                                                                            val alertDialog =
                                                                                                AlertDialog.Builder(
                                                                                                    this@registro_empresa
                                                                                                )
                                                                                                    .setTitle(
                                                                                                        "Cuenta registrada"
                                                                                                    )
                                                                                                    .setMessage(
                                                                                                        "Tu cuenta ha sido creada, revise su correo electrónico para más información."
                                                                                                    )
                                                                                                    .setPositiveButton(
                                                                                                        "Aceptar",
                                                                                                        null
                                                                                                    )
                                                                                                    .create()

                                                                                            alertDialog.setOnDismissListener { _ ->
                                                                                                val login =
                                                                                                    Intent(
                                                                                                        this@registro_empresa,
                                                                                                        login::class.java
                                                                                                    )
                                                                                                login.flags =
                                                                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                                                startActivity(
                                                                                                    login
                                                                                                )
                                                                                            }
                                                                                            alertDialog.show()
                                                                                            txtNombreEmpleador.setText(
                                                                                                ""
                                                                                            )
                                                                                            txtEmpresaEmpleador.setText(
                                                                                                ""
                                                                                            )
                                                                                            txtCorreoEmpleador.setText(
                                                                                                ""
                                                                                            )
                                                                                            txtContrasenaEmpleador.setText(
                                                                                                ""
                                                                                            )
                                                                                            txtTelefonoEmpleador.setText(
                                                                                                ""
                                                                                            )
                                                                                            txtDireccionEmpleador.setText(
                                                                                                ""
                                                                                            )
                                                                                            txtSitioWebEmpleador.setText(
                                                                                                ""
                                                                                            )
                                                                                            imgFotoDePerfilEmpleador.setImageDrawable(
                                                                                                null
                                                                                            )
                                                                                        } else {
                                                                                            Toast.makeText(
                                                                                                this@registro_empresa,
                                                                                                "Error al guardar datos en la base de datos: ${dbTask.exception?.message}",
                                                                                                Toast.LENGTH_LONG
                                                                                            ).show()
                                                                                        }
                                                                                    }
                                                                            } else {
                                                                                Toast.makeText(
                                                                                    this@registro_empresa,
                                                                                    "Error al actualizar nombre en Firebase: ${updateTask.exception?.message}",
                                                                                    Toast.LENGTH_LONG
                                                                                ).show()
                                                                            }
                                                                        }
                                                                } else {
                                                                    // Error al registrar en Firebase
                                                                    Toast.makeText(
                                                                        this@registro_empresa,
                                                                        "Error al registrar en Firebase: ${task.exception?.message}",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    btnCrearCuentaEmpleador.isEnabled =
                                                                        true
                                                                }
                                                            }
                                                    }

                                                    //hasta aqui es lo de firebase

                                                }


                                            }
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    this@registro_empresa,
                                                    "Error al crear el usuario.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@registro_empresa,
                                "Ocurrió un error al crear la cuenta. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")
                        }
                    } finally {
                        // Habilitar el botón nuevamente después de completar la coroutine
                        withContext(Dispatchers.Main) {
                            btnCrearCuentaEmpleador.isEnabled = true
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val comprobarSiExisteCorreoEmpleador =
                            objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ? ")!!
                        comprobarSiExisteCorreoEmpleador.setString(1, CorreoEmpleador)

                        val comprobarSiExisteCorreoSolicitante =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ? ")!!
                        comprobarSiExisteCorreoSolicitante.setString(1, CorreoEmpleador)

                        val comprobarSiExistetelefonoSolicitante =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE Telefono = ? ")!!
                        comprobarSiExistetelefonoSolicitante.setString(1, TelefoEmpleador)

                        val comprobarSiExistetelefonoEmpleador =
                            objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE NumeroTelefono = ? ")!!
                        comprobarSiExistetelefonoEmpleador.setString(1, TelefoEmpleador)

                        val existeCorreoSolicitante =
                            comprobarSiExisteCorreoSolicitante.executeQuery()
                        val existeCorreoEmpleador = comprobarSiExisteCorreoEmpleador.executeQuery()

                        val existeTelefonoSolicitante =
                            comprobarSiExistetelefonoSolicitante.executeQuery()

                        val existeTelefonoEmpleador =
                            comprobarSiExistetelefonoEmpleador.executeQuery()

                        if (existeCorreoSolicitante.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese correo electrónico, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else if (existeCorreoEmpleador.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese correo electrónico, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else if (existeTelefonoSolicitante.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese número de teléfono, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else if (existeTelefonoEmpleador.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese número de teléfono, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnCrearCuentaEmpleador.isEnabled = true
                            }
                        } else {
                            // Encripto la contraseña usando la función de encriptación
                            val contrasenaEncriptada =
                                hashSHA256(txtContrasenaEmpleador.text.toString())

                            withContext(Dispatchers.Main) {
                                val bitmap =
                                    (imgFotoDePerfilEmpleador.drawable as BitmapDrawable).bitmap
                                subirimagenFirebase(bitmap) { imageUrl ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {

                                            val DepartamentoNombre =
                                                spDepartamentos.selectedItem.toString()

                                            // Obtener el id_medicamento desde el Spinner
                                            val Departamento =
                                                obtenerDepartamentos() // Se asume que puedes obtener la lista de medicamentos aquí
                                            val DepartamentoSeleccionado =
                                                Departamento.find { it.Nombre == DepartamentoNombre }
                                            val idDepartamento =
                                                DepartamentoSeleccionado!!.Id_departamento

                                            val crearUsuario = objConexion?.prepareStatement(
                                                "INSERT INTO EMPLEADOR (IdEmpleador, NombreEmpresa, CorreoElectronico, NumeroTelefono, Direccion, SitioWeb, NombreRepresentante, IdDepartamento, Contrasena, Estado, Foto) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )"
                                            )!!
                                            crearUsuario.setString(1, uuid)
                                            crearUsuario.setString(
                                                2,
                                                txtEmpresaEmpleador.text.toString()
                                            )
                                            crearUsuario.setString(
                                                3,
                                                txtCorreoEmpleador.text.toString()
                                            )
                                            crearUsuario.setString(
                                                4,
                                                TelefoEmpleador
                                            )
                                            crearUsuario.setString(
                                                5,
                                                txtDireccionEmpleador.text.toString()
                                            )
                                            crearUsuario.setString(6, SitioWebEmpleador)
                                            crearUsuario.setString(
                                                7,
                                                txtNombreEmpleador.text.toString()
                                            )
                                            crearUsuario.setInt(
                                                8,
                                                idDepartamento
                                            )
                                            crearUsuario.setString(9, contrasenaEncriptada)
                                            crearUsuario.setString(10, "Activo")
                                            crearUsuario.setString(11, imageUrl)

                                            crearUsuario.executeUpdate()

                                            //TODO: ESTA PARTE ES PARA QUE EL NOMBRE Y CONTRASEÑA SE ENVIEN A FIREBASE

                                            withContext(Dispatchers.Main) {

                                                FirebaseAuth.getInstance()
                                                    .createUserWithEmailAndPassword(
                                                        CorreoEmpleador,
                                                        ContrasenaEmpleador
                                                    )
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            // Usuario creado en Firebase Authentication
                                                            val user =
                                                                FirebaseAuth.getInstance().currentUser
                                                            val profileUpdates =
                                                                UserProfileChangeRequest.Builder()
                                                                    .setDisplayName(nombreEmpleador)  // Establecer el nombre del usuario
                                                                    .build()

                                                            // Actualizar el perfil del usuario con el nombre
                                                            user?.updateProfile(profileUpdates)
                                                                ?.addOnCompleteListener { updateTask ->
                                                                    if (updateTask.isSuccessful) {
                                                                        // Obtener el UID del usuario recién creado
                                                                        val userId = user.uid

                                                                        // Crear referencia a la base de datos de Firebase
                                                                        val databaseRef =
                                                                            com.google.firebase.Firebase.database.reference

                                                                        // Datos que deseas guardar en la base de datos
                                                                        val userMap = mapOf(
                                                                            "nombre" to nombreEmpleador,
                                                                            "correo" to CorreoEmpleador,
                                                                            "rol" to "empleador" // Puedes agregar un campo 'rol' si es necesario
                                                                        )

                                                                        // Guardar los datos del usuario en la Realtime Database bajo 'solicitantes/{userId}'
                                                                        databaseRef.child("Usuarios")
                                                                            .child(userId)
                                                                            .setValue(userMap)
                                                                            .addOnCompleteListener { dbTask ->
                                                                                if (dbTask.isSuccessful) {
                                                                                    val alertDialog =
                                                                                        AlertDialog.Builder(
                                                                                            this@registro_empresa
                                                                                        )
                                                                                            .setTitle(
                                                                                                "Cuenta registrada"
                                                                                            )
                                                                                            .setMessage(
                                                                                                "Tu cuenta ha sido creada."
                                                                                            )
                                                                                            .setPositiveButton(
                                                                                                "Aceptar",
                                                                                                null
                                                                                            )
                                                                                            .create()

                                                                                    alertDialog.setOnDismissListener { _ ->
                                                                                        val login =
                                                                                            Intent(
                                                                                                this@registro_empresa,
                                                                                                login::class.java
                                                                                            )
                                                                                        login.flags =
                                                                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                                        startActivity(
                                                                                            login
                                                                                        )
                                                                                    }
                                                                                    alertDialog.show()
                                                                                    txtNombreEmpleador.setText(
                                                                                        ""
                                                                                    )
                                                                                    txtEmpresaEmpleador.setText(
                                                                                        ""
                                                                                    )
                                                                                    txtCorreoEmpleador.setText(
                                                                                        ""
                                                                                    )
                                                                                    txtContrasenaEmpleador.setText(
                                                                                        ""
                                                                                    )
                                                                                    txtTelefonoEmpleador.setText(
                                                                                        ""
                                                                                    )
                                                                                    txtDireccionEmpleador.setText(
                                                                                        ""
                                                                                    )
                                                                                    txtSitioWebEmpleador.setText(
                                                                                        ""
                                                                                    )
                                                                                    imgFotoDePerfilEmpleador.setImageDrawable(
                                                                                        null
                                                                                    )
                                                                                } else {
                                                                                    Toast.makeText(
                                                                                        this@registro_empresa,
                                                                                        "Error al guardar datos en la base de datos: ${dbTask.exception?.message}",
                                                                                        Toast.LENGTH_LONG
                                                                                    ).show()
                                                                                }
                                                                            }
                                                                    } else {
                                                                        Toast.makeText(
                                                                            this@registro_empresa,
                                                                            "Error al actualizar nombre en Firebase: ${updateTask.exception?.message}",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    }
                                                                }
                                                        } else {
                                                            // Error al registrar en Firebase
                                                            Toast.makeText(
                                                                this@registro_empresa,
                                                                "Error al registrar en Firebase: ${task.exception?.message}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            btnCrearCuentaEmpleador.isEnabled = true
                                                        }
                                                    }
                                            }

                                            //hasta aqui es lo de firebase

                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(
                                                    this@registro_empresa,
                                                    "Ocurrió un error al crear la cuenta. Por favor, intente nuevamente.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                println("Error: ${e.message}")
                                            }
                                        } finally {
                                            // Habilitar el botón nuevamente después de completar la coroutine
                                            withContext(Dispatchers.Main) {
                                                btnCrearCuentaEmpleador.isEnabled = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@registro_empresa,
                                "Ocurrió un error al crear la cuenta. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")
                        }
                    } finally {
                        // Habilitar el botón nuevamente después de completar la coroutine
                        withContext(Dispatchers.Main) {
                            btnCrearCuentaEmpleador.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no está aceptado, entonces se lo pedimos
            requestCameraPermission()
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, codigo_opcion_tomar_foto)
        }
    }


    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no está aceptado, entonces se lo pedimos
            requestStoragePermission()
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, codigo_opcion_galeria)
        }
    }

    fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.READ_MEDIA_IMAGES
            )
        ) {
            // El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
            // Aquí podrías mostrar un diálogo explicativo al usuario.
        } else {
            // El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                STORAGE_REQUEST_CODE
            )
        }
    }

    fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.CAMERA
            )
        ) {
            // El usuario ya ha rechazado el permiso anteriormente, informarle sobre esto.
        } else {
            // El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Abrimos la galería
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, codigo_opcion_galeria)
                } else {
                    Toast.makeText(
                        this,
                        "Permiso de almacenamiento denegado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Si decides mantener el manejo del permiso de la cámara:
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // El usuario ha aceptado el permiso, abrimos la cámara:
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, codigo_opcion_tomar_foto)
                } else {
                    // El usuario ha rechazado el permiso para la cámara, manejar la lógica aquí.
                }
            }

            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }

    // Esta función onActivityResult se encarga de capturar lo que pasa al abrir la galería o la cámara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                codigo_opcion_galeria -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        subirimagenFirebase(imageBitmap) { url ->
                            miPathEmpresa = url
                            imgFotoDePerfilEmpleador.setImageURI(it)
                            fotoSubida = true // Foto subida correctamente
                        }
                    }
                }

                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPathEmpresa = url
                            imgFotoDePerfilEmpleador.setImageBitmap(it)
                            fotoSubida = true // Foto subida correctamente
                        }
                    }
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
                this@registro_empresa,
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