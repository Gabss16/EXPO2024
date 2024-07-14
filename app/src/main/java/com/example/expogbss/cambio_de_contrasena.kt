package com.example.expogbss

import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import modelo.ClaseConexion
import java.security.MessageDigest

class cambio_de_contrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cambio_de_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Códigos que se traen de la pantalla anterior
        val correo = ingresarCorreoRecupContrasena.correoIngresado
        val codigoRecuperacion = ingresarCorreoRecupContrasena.codigo

        val btnCambiarContrasena = findViewById<Button>(R.id.btnCambiarContrasena)
        val txtContrasenaNueva = findViewById<EditText>(R.id.txtnuevacontrasena)

        val verificarContraseña = Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

        //Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }


        btnCambiarContrasena.setOnClickListener {
            val contrasenaNueva = txtContrasenaNueva.text.toString()

        if (contrasenaNueva.isEmpty()) {
            Toast.makeText(
                this@cambio_de_contrasena, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT
            ).show()
        } else if (!verificarContraseña.matches(contrasenaNueva)) {
            Toast.makeText(
                this@cambio_de_contrasena,
                "La contraseña debe tener al menos 6 caracteres y un carácter especial",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val objConexion = ClaseConexion().cadenaConexion()

            // Encripto la contraseña usando la función de encriptación
            val contrasenaEncriptada = hashSHA256(contrasenaNueva)

            val actualizarcontraseña =
                objConexion?.prepareStatement("UPDATE EMPLEADOR SET contrasena = ? WHERE CorreoElectronico = ?")!!
            actualizarcontraseña.setString(1, contrasenaEncriptada)
            actualizarcontraseña.setString(2, correo)
            actualizarcontraseña.executeQuery()

        }
    }
    }
}