package com.example.expogbss

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
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
import java.security.MessageDigest

class registroSolicitante : AppCompatActivity() {
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
                { view, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
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

        val spAreaDeTrabajoSolicitante = findViewById<Spinner>(R.id.spAreaDeTrabajoSolicitante)
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
        val adaptadorDeAreas =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoAreas)
        spAreaDeTrabajoSolicitante.adapter = adaptadorDeAreas

        val imgFotoDePerfilSolicitante = findViewById<ImageView>(R.id.imgFotoDePerfilSolicitante)
        val btnSubirDesdeGaleriaSolicitante =
            findViewById<Button>(R.id.btnSubirDesdeGaleriaSolicitante)
        val btnTomarFotoSolicitante = findViewById<Button>(R.id.btnTomarFotoSolicitante)
        val btnSubirCV = findViewById<ImageView>(R.id.btnSubirCV)
        val btnCrearCuentaSolicitante = findViewById<ImageButton>(R.id.btnCrearCuentaSolicitante)

        //Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        //Programo el boton para registrar a un solicitante


        btnCrearCuentaSolicitante.setOnClickListener {

            //Mando a llamar a cada Edit Text
        }
    }
}