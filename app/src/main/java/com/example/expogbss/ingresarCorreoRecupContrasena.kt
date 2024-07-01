package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
class ingresarCorreoRecupContrasena : AppCompatActivity() {

    private val correoEnviado = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ingresar_correo_recup_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnEnviar = findViewById<Button>(R.id.btnEnviarCodigoRecuperacion)
        var codigoRecuperacion = (1000..9999).random()
        val txtCorreo = findViewById<EditText>(R.id.txtIngresarCorreoRecuperacion)

        btnEnviar.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val correo = txtCorreo.text.toString()
                recuperarContrasena(correo, "Recuperación de contraseña", "Hola este es su codigo de recuperacion: $codigoRecuperacion")
                correoEnviado.emit(true)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            correoEnviado.collect { enviado ->
                if (enviado) {
                    val pantallaIngresoCodigo = Intent(this@ingresarCorreoRecupContrasena, recoveryCode::class.java)
                    startActivity(pantallaIngresoCodigo)
                }
            }
        }
    }
}