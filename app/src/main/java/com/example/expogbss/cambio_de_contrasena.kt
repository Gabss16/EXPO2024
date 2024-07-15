package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
        val correoRecogido = ingresarCorreoRecupContrasena.correoIngresado

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
                Toast.makeText(
                    this@cambio_de_contrasena,
                    "La contraseña no puede estar vacía",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (!verificarContraseña.matches(contrasenaNueva)) {
                Toast.makeText(
                    this@cambio_de_contrasena,
                    "La contraseña debe tener al menos 6 caracteres y un carácter especial",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Uso de corrutinas para manejar las operaciones de base de datos
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()

                    // Encripto la contraseña usando la función de encriptación
                    val contrasenaEncriptada = hashSHA256(contrasenaNueva)

                    // Verificar si es EMPLEADOR
                    if (ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.CambiarContraEmpleador) {
                        try {
                            val actualizarContraseñaEmpleador = objConexion?.prepareStatement(
                                "UPDATE EMPLEADOR SET Contrasena = ? WHERE CorreoElectronico = ?"
                            )!!
                            actualizarContraseñaEmpleador.setString(1, contrasenaEncriptada)
                            actualizarContraseñaEmpleador.setString(2, ingresarCorreoRecupContrasena.correoIngresado)

                            val filasActualizadasEmpleador = actualizarContraseñaEmpleador.executeUpdate()

                            withContext(Dispatchers.Main) {
                                if (filasActualizadasEmpleador > 0) {
                                    Toast.makeText(
                                        this@cambio_de_contrasena,
                                        "Contraseña de Empleador actualizada correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    try {
                                        startActivity(Intent(this@cambio_de_contrasena, passwordResetSuccessful::class.java))
                                        finish()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(
                                            this@cambio_de_contrasena,
                                            "Error al navegar a la pantalla de éxito",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@cambio_de_contrasena,
                                        "No se encontró el correo electrónico del Empleador",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: SQLException) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@cambio_de_contrasena,
                                    "Error al actualizar la contraseña del Empleador",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    // Verificar si es SOLICITANTE
                    if (ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.CambiarContraSolicitante) {
                        try {
                            val actualizarContraseñaSolicitante = objConexion?.prepareStatement(
                                "UPDATE SOLICITANTE SET Contrasena = ? WHERE CorreoElectronico = ?"
                            )!!
                            actualizarContraseñaSolicitante.setString(1, contrasenaEncriptada)
                            actualizarContraseñaSolicitante.setString(2, correoRecogido)

                            val filasActualizadasSolicitante = actualizarContraseñaSolicitante.executeUpdate()

                            withContext(Dispatchers.Main) {
                                if (filasActualizadasSolicitante > 0) {
                                    Toast.makeText(
                                        this@cambio_de_contrasena,
                                        "Contraseña de Solicitante actualizada correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    try {
                                        startActivity(Intent(this@cambio_de_contrasena, passwordResetSuccessful::class.java))
                                        finish()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(
                                            this@cambio_de_contrasena,
                                            "Error al navegar a la pantalla de éxito",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@cambio_de_contrasena,
                                        "No se encontró el correo electrónico del Solicitante",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: SQLException) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@cambio_de_contrasena,
                                    "Error al actualizar la contraseña del Solicitante",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                } catch (e: SQLException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@cambio_de_contrasena,
                            "Error al actualizar la contraseña",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
