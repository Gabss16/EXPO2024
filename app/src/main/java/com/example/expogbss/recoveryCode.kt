package com.example.expogbss

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import android.text.TextWatcher;
import android.text.Editable;
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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


        val correo = intent.getStringExtra("correo")
        println("Correo recibido: $correo")

        val codigoRecuperacion = intent.getIntExtra("codigoRecuperacion", 0)
        println("Código de recuperación recibido: $codigoRecuperacion")

        val codigoIngresado = "${txtPrimerDigito.text}${txtSegundoDigito.text}${txtTercerDigito.text}${txtCuartoDigito.text}"
        println(codigoIngresado)


    }
}

