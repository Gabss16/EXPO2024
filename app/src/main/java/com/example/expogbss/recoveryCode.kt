package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion

class recoveryCode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recovery_code)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtPrimerDigito = findViewById<EditText>(R.id.txtPrimerDigito)
        val txtSegundoDigito = findViewById<EditText>(R.id.txtSegundoDigito)
        val txtTercerDigito = findViewById<EditText>(R.id.txtTercerDigito)
        val txtCuartoDigito = findViewById<EditText>(R.id.txtCuartoDigito)

        val btnReenviarCodigo = findViewById<Button>(R.id.btnVolverAEnviarCodigo)

        val correo = intent.getStringExtra("correo")!!
        println("Correo recibido: $correo")

        val codigoRecuperacion = intent.getIntExtra("codigoRecuperacion", 0)
        println("Código de recuperación recibido: $codigoRecuperacion")

//        val primerDigito = txtPrimerDigito.text.toString()
//        val segundoDigito = txtSegundoDigito.text.toString()
//        val tercerDigito = txtTercerDigito.text.toString()
//        val cuartoDigito = txtCuartoDigito.text.toString()
//
//        val codigoIngresado = "$primerDigito$segundoDigito$tercerDigito$cuartoDigito"
//        println("Código ingresado: $codigoIngresado")

        btnReenviarCodigo.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()

                val correoEnviado = recuperarContrasena(
                    correo,
                    "Recuperación de contraseña",
                    "Hola este es su codigo de recuperacion: $codigoRecuperacion"
                )
                if (correoEnviado) {
                    val pantallaIngresoCodigo = Intent(
                        this@recoveryCode, recoveryCode::class.java
                    )
                    startActivity(pantallaIngresoCodigo)
                    finish()
                } else {
                    Toast.makeText(
                        this@recoveryCode,
                        "Hubo un error al enviar el correo, intenta de nuevo",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }


        }

    }
}

