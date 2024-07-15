package com.example.expogbss

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import modelo.ClaseConexion
import java.security.MessageDigest
import java.sql.SQLException

class cambio_de_contrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cambio_de_contrasena)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Datos traídos de la pantalla anterior
        val correo = ingresarCorreoRecupContrasena.correoIngresado
        val codigoRecuperacion = ingresarCorreoRecupContrasena.codigo

        val btnCambiarContrasena = findViewById<Button>(R.id.btnCambiarContrasena)
        val txtContrasenaNueva = findViewById<EditText>(R.id.txtnuevacontrasena)

        val verificarContraseña = Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

        // Función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnCambiarContrasena.setOnClickListener {
            val contrasenaNueva = txtContrasenaNueva.text.toString()

            if (contrasenaNueva.isEmpty()) {
                Toast.makeText(this@cambio_de_contrasena, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
            } else if (!verificarContraseña.matches(contrasenaNueva)) {
                Toast.makeText(this@cambio_de_contrasena, "La contraseña debe tener al menos 6 caracteres y un carácter especial", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()
                    if (objConexion != null) {
                        // Encripto la contraseña usando la función de encriptación
                        val contrasenaEncriptada = hashSHA256(contrasenaNueva)

                        val actualizarContraseña = objConexion.prepareStatement(
                            "UPDATE EMPLEADOR SET contrasena = ? WHERE CorreoElectronico = ?"
                        )
                        actualizarContraseña.setString(1, contrasenaEncriptada)
                        actualizarContraseña.setString(2, correo)

                        val filasActualizadas = actualizarContraseña.executeUpdate()
                        if (filasActualizadas > 0) {
                            Toast.makeText(this@cambio_de_contrasena, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@cambio_de_contrasena, "No se encontró el correo electrónico", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@cambio_de_contrasena, "Error en la conexión a la base de datos", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                    Toast.makeText(this@cambio_de_contrasena, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
