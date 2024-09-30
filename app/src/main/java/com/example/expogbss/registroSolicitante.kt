package com.example.expogbss

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import modelo.AreaDeTrabajo
import modelo.Departamento
import java.io.ByteArrayOutputStream
import java.util.UUID

//TODO Terminar insert cuando esté completo el diseño
class registroSolicitante : AppCompatActivity() {
    val codigo_opcion_galeria = 102
    val codigo_opcion_tomar_foto = 103
    val CAMERA_REQUEST_CODE = 0
    val STORAGE_REQUEST_CODE = 1

    lateinit var imgFotoDePerfilSolicitante: ImageView
    lateinit var miPath: String
    val uuid = UUID.randomUUID().toString()
    private var fotoSubida = false
    private var fechaNacimientoSeleccionada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_solicitante)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //1 Mandar a llamar a todos los elementos en pantalla
        val txtNombreSolicitante = findViewById<EditText>(R.id.txtNombreSolicitante)
        val txtCorreoSolicitante = findViewById<EditText>(R.id.txtCorreoSolicitante)
        val txtConstrasenaSolicitante = findViewById<EditText>(R.id.txtContrasenaSolicitante)
        val txtTelefonoSolicitante = findViewById<EditText>(R.id.txtTelefonoSolicitante)
        val txtDireccionSolicitante = findViewById<EditText>(R.id.txtDireccionSolicitante)
        val txtHabilidadesSolicitante = findViewById<EditText>(R.id.txtHabilidadesLaborales)
        val spDepartamentoSolicitante = findViewById<Spinner>(R.id.spDepartamentoSolicitante)
        val spGeneroSolicitante = findViewById<Spinner>(R.id.spGeneroSolicitante)
        val txtFechaSolicitante = findViewById<EditText>(R.id.txtFechaSolicitante)
        val spAreaDeTrabajoSolicitante = findViewById<Spinner>(R.id.spAreaDeTrabajoSolicitante)
        val spEstadoSolicitante = findViewById<Spinner>(R.id.spSituacionLaboralSolicitante)
        val btnTomarFotoSolicitante = findViewById<Button>(R.id.btnTomarFotoSolicitante)
        val btnCrearCuentaSolicitante = findViewById<ImageButton>(R.id.btnCrearCuentaSolicitante)
        val btnSubirDesdeGaleriaSolicitante = findViewById<Button>(R.id.btnSubirDesdeGaleriaSolicitante)
        imgFotoDePerfilSolicitante = findViewById(R.id.imgFotoDePerfilSolicitante)

        val btnSalir8 = findViewById<ImageButton>(R.id.btnSalir8)

        btnSalir8.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
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
                    this@registroSolicitante, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    Departamento
                )
                spDepartamentoSolicitante.adapter = adapter
            }
        }


        // Mostrar el calendario al hacer click en el EditText txtFechaNacimientoPaciente
        txtFechaSolicitante.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            // Calcular la fecha máxima (hace 18 años a partir de hoy)
            val fechaMaxima = Calendar.getInstance()
            fechaMaxima.set(anio - 18, mes, dia)

            val datePickerDialog = DatePickerDialog(
                this, { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada =
                        "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaSolicitante.setText(fechaSeleccionada)
                    fechaNacimientoSeleccionada = fechaSeleccionada
                }, anio, mes, dia
            )

            // Configurar la fecha máxima a hace 18 años a partir de hoy
            datePickerDialog.datePicker.maxDate = fechaMaxima.timeInMillis

            datePickerDialog.show()
        }



        val listadoGeneros = listOf(
            "Masculino", "Femenino", "Prefiero no decirlo"
        )
        val adaptadorDeGeneros =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoGeneros)
        spGeneroSolicitante.adapter = adaptadorDeGeneros




        // Función para hacer el select del Area de trabajo
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
                    this@registroSolicitante, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    AreaDeTrabajo
                )
                spAreaDeTrabajoSolicitante.adapter = adapter
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

        // Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnCrearCuentaSolicitante.setOnClickListener {

            // mando a llamar a cada textview
            val nombreSolicitante = txtNombreSolicitante.text.toString()
            val correoSolicitante = txtCorreoSolicitante.text.toString()
            val contrasenaSolicitante = txtConstrasenaSolicitante.text.toString()
            val telefonoSolicitante = txtTelefonoSolicitante.text.toString()
            val direccionSolicitante = txtDireccionSolicitante.text.toString()
            val fechaSolicitante = txtFechaSolicitante.text.toString()

            val VerificarTelefono = Regex("^\\d{4}-\\d{4}\$")
            val verificarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val verificarContraseña =
                Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

            // Validaciones de campos vacíos y cosas por ese estilo
            if (nombreSolicitante.isEmpty() || correoSolicitante.isEmpty() || contrasenaSolicitante.isEmpty() || telefonoSolicitante.isEmpty() || direccionSolicitante.isEmpty() || fechaSolicitante.isEmpty()) {
                Toast.makeText(
                    this@registroSolicitante,
                    "Por favor, llenar los espacios obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!VerificarTelefono.matches(telefonoSolicitante)) {
                Toast.makeText(
                    this@registroSolicitante,
                    "Ingresar un número de teléfono válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarCorreo.matches(correoSolicitante)) {
                Toast.makeText(
                    this@registroSolicitante,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarContraseña.matches(contrasenaSolicitante)) {
                Toast.makeText(
                    this@registroSolicitante,
                    "La contraseña debe contener al menos un caracter especial y tener más de 6 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!fotoSubida) {
                Toast.makeText(
                    this@registroSolicitante,
                    "Por favor, sube una foto de perfil.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val comprobarSiExisteCorreo =
                            objConexion?.prepareStatement("SELECT * FROM Empleador WHERE CorreoElectronico = ? ")!!
                        comprobarSiExisteCorreo.setString(1, correoSolicitante)

                        val existeCorreoSolicitante = comprobarSiExisteCorreo.executeQuery()

                        if (existeCorreoSolicitante.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registroSolicitante,
                                    "Ya existe alguien con ese correo electrónico, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            println("Esto sale en la fecha: $fechaNacimientoSeleccionada")

                            // Encripto la contraseña usando la función de encriptación
                            val contrasenaEncriptada =
                                hashSHA256(txtConstrasenaSolicitante.text.toString().trim())

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

                            // Creo una variable que contenga un PrepareStatement
                            val crearUsuario = objConexion?.prepareStatement(
                                "INSERT INTO SOLICITANTE (IdSolicitante, Nombre, CorreoElectronico, Telefono, Direccion,IdDepartamento, FechaDeNacimiento, Estado, Genero ,IdAreaDeTrabajo, Habilidades,Curriculum,Foto, Contrasena) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?)"
                            )!!
                            crearUsuario.setString(1, uuid)
                            crearUsuario.setString(2, txtNombreSolicitante.text.toString().trim())
                            crearUsuario.setString(3, txtCorreoSolicitante.text.toString().trim())
                            crearUsuario.setString(4, txtTelefonoSolicitante.text.toString().trim())
                            crearUsuario.setString(5, txtDireccionSolicitante.text.toString().trim())
                            crearUsuario.setInt(
                                6, idDepartamento
                            )
                            crearUsuario.setString(7, fechaNacimientoSeleccionada)
                            crearUsuario.setString(8, spEstadoSolicitante.selectedItem.toString())
                            crearUsuario.setString(9, spGeneroSolicitante.selectedItem.toString())
                            crearUsuario.setInt(
                                10,
                                idAreaDeTrabajo
                            )
                            crearUsuario.setString(
                                11,
                                txtHabilidadesSolicitante.text.toString().trim()
                            )
                            crearUsuario.setBlob(
                                12,
                                objConexion.createBlob()
                            ); // Asignar un BLOB vacío
                            crearUsuario.setString(13, miPath)
                            crearUsuario.setString(14, contrasenaEncriptada)

                            crearUsuario.executeUpdate()

                            withContext(Dispatchers.Main) {
                                val alertDialog = AlertDialog.Builder(this@registroSolicitante)
                                    .setTitle("Cuenta registrada")
                                    .setMessage("Tu cuenta ha sido creada.")
                                    .setPositiveButton("Aceptar", null)
                                    .create()

                                alertDialog.setOnDismissListener { _ ->
                                    val login = Intent(this@registroSolicitante, login::class.java)
                                    login.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(login)
                                }
                                alertDialog.show()
                                txtNombreSolicitante.setText("")
                                txtCorreoSolicitante.setText("")
                                txtConstrasenaSolicitante.setText("")
                                txtTelefonoSolicitante.setText("")
                                txtDireccionSolicitante.setText("")
                                txtFechaSolicitante.setText("")
                                imgFotoDePerfilSolicitante.setImageDrawable(null)
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@registroSolicitante,
                                "Ocurrió un error al crear la cuenta. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")

                        }
                    }
                }
            }
        }

        btnSubirDesdeGaleriaSolicitante.setOnClickListener {
            // Al darle clic al botón de la galería pedimos los permisos primero
            checkStoragePermission()
        }



        btnTomarFotoSolicitante.setOnClickListener {
            // Al darle clic al botón de la cámara pedimos los permisos primero
            checkCameraPermission()
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
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE
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

    fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.CAMERA
            )
        ) {
            // El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
        } else {
            // El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        }
    }

    fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            // El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //El usuario ha aceptado el permiso, no tiene porqué darle de nuevo al botón, podemos lanzar la funcionalidad desde aquí.
                    //Abrimos la camara:
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, codigo_opcion_tomar_foto)
                } else {
                    //El usuario ha rechazado el permiso, podemos desactivar la funcionalidad o mostrar una vista/diálogo.
                }

                return
            }

            STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Abrimos la galeria
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, codigo_opcion_galeria)
                } else {
                    Toast.makeText(
                        this,
                        "Permiso de almacenamiento denegado",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }

    // Esta función onActivityResult se encarga de capturar lo que pasa al abrir la geleria o la camara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                codigo_opcion_galeria -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val imageBitmap =
                            MediaStore.Images.Media.getBitmap(contentResolver, it)
                        subirimagenFirebase(imageBitmap) { url ->
                            miPath = url
                            imgFotoDePerfilSolicitante.setImageURI(it)
                            fotoSubida = true // Foto subida correctamente
                        }
                    }
                }


                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPath = url
                            imgFotoDePerfilSolicitante.setImageBitmap(it)
                            fotoSubida = true // Foto subida correctamente

                        }
                    }
                }
            }
        }
    }

    //Subir la imagen a Firebase Storage
    private fun subirimagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${uuid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(
                this@registroSolicitante,
                "Error al subir la imagen",
                Toast.LENGTH_SHORT
            )
                .show()

        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }
}
