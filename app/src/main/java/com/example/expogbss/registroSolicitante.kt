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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.AreaDeTrabajo
import modelo.ClaseConexion
import java.security.MessageDigest
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID


class registroSolicitante : AppCompatActivity() {
    val codigo_opcion_galeria = 102
    val codigo_opcion_tomar_foto = 103
    val CAMERA_REQUEST_CODE = 0
    val STORAGE_REQUEST_CODE = 1

    lateinit var imgFotoDePerfilSolicitante: ImageView
    lateinit var miPath: String
    val uuid = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val spDepartamentoSolicitante = findViewById<Spinner>(R.id.spDepartamentoSolicitante)

        val listadoDepartamentos = listOf(
            "Ahuachapán",
            "Cabañas",
            "Chalatenango",
            "Cuscatlán",
            "La Libertad",
            "Morazán",
            "La Paz",
            "Santa Ana",
            "San Miguel",
            "San Vicente",
            "San Salvador",
            "Sonsonate",
            "La Unión",
            "Usulután"
        )
        val adaptadorDeLinea =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoDepartamentos)
        spDepartamentoSolicitante.adapter = adaptadorDeLinea

        val txtFechaSolicitante = findViewById<EditText>(R.id.txtFechaSolicitante)
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
                this,
                { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada =
                        "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                    txtFechaSolicitante.setText(fechaSeleccionada)
                },
                anio, mes, dia
            )

            // Configurar la fecha máxima a hace 18 años a partir de hoy
            datePickerDialog.datePicker.maxDate = fechaMaxima.timeInMillis

            datePickerDialog.show()
        }

        val spGeneroSolicitante = findViewById<Spinner>(R.id.spGeneroSolicitante)
        val listadoGeneros = listOf(
            "Masculino",
            "Femenino",
            "Prefiero no decirlo"
        )
        val adaptadorDeGeneros =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoGeneros)
        spGeneroSolicitante.adapter = adaptadorDeGeneros

        // Función para hacer el select de las áreas de trabajo
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

        val spAreaDeTrabajoSolicitante = findViewById<Spinner>(R.id.spAreaDeTrabajoSolicitante)
        CoroutineScope(Dispatchers.IO).launch {
            val listadoDeAreasDeTrabajo = obtenerAreasDeTrabajo()
            val AreasDeTrabajo = listadoDeAreasDeTrabajo.map { it.NombreAreaDetrabajo }

            withContext(Dispatchers.Main) {
                // Creo la configuración del adaptador
                // El Adaptador solicita tres cosas: contexto, layout y los datos
                val adapter = ArrayAdapter(
                    this@registroSolicitante, // Usar el contexto adecuado
                    android.R.layout.simple_spinner_dropdown_item,
                    AreasDeTrabajo
                )
                spAreaDeTrabajoSolicitante.adapter = adapter
            }
        }

        imgFotoDePerfilSolicitante = findViewById(R.id.imgFotoDePerfilSolicitante)
        val btnSubirDesdeGaleriaSolicitante =
            findViewById<Button>(R.id.btnSubirDesdeGaleriaSolicitante)
        val btnTomarFotoSolicitante = findViewById<Button>(R.id.btnTomarFotoSolicitante)
        val btnCrearCuentaSolicitante = findViewById<ImageButton>(R.id.btnCrearCuentaSolicitante)

        // Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnSubirDesdeGaleriaSolicitante.setOnClickListener {
            // Al darle clic al botón de la galería pedimos los permisos primero
            checkStoragePermission()
        }
        btnTomarFotoSolicitante.setOnClickListener {
            // Al darle clic al botón de la cámara pedimos los permisos primero
            checkCameraPermission()
        }

        btnCrearCuentaSolicitante.setOnClickListener {
            // Mando a llamar a cada EditText
        }
    }

    fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no está aceptado, entonces se lo pedimos
            requestCameraPermission()
        } else {

        }
    }

    fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no está aceptado, entonces se lo pedimos
            requestStoragePermission()
        } else {

        }
    }

    fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
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
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
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
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
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
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            else -> {
                // Este else lo dejamos por si sale un permiso que no teníamos controlado.
            }
        }
    }
    //Esta función onActivityResult se encarga de capturar lo que pasa al abrir la geleria o la camara
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                codigo_opcion_galeria -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        subirimagenFirebase(imageBitmap) { url gs://agalo-e4597.appspot.com
                            miPath = url
                            imgFotoDePerfilSolicitante.setImageURI(it)
                        }
                    }
                }


                codigo_opcion_tomar_foto -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        subirimagenFirebase(it) { url ->
                            miPath = url
                            imageView.setImageBitmap(it)
                        }
                    }
                }
            }
        }
    }
}
