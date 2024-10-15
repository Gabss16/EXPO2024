package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class cambiar_contrasena_Solicitante : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cambiar_contrasena_solicitante)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtContrasenaActual = findViewById<EditText>(R.id.txtContrasenaActualSolicitante)
        val txtNuevaContrasena = findViewById<EditText>(R.id.txtNuevaContrasenaSolicitante)
        val txtReIngresarNuevaContrasenaEmpleador = findViewById<EditText>(R.id.txtRepetirNuevaContrasenaSolicitante)
        val btnEditarContrasena = findViewById<ImageView>(R.id.btnUpdateContrasenaSolicitante)
        val btnSalir1 = findViewById<ImageButton>(R.id.btnSalir1)

        var isPassword1Visible = false
        var isPassword2Visible = false
        var isPassword3Visible = false

        val passwordview1 = txtContrasenaActual
        val passwordview2 = txtNuevaContrasena
        val passwordview3 = txtReIngresarNuevaContrasenaEmpleador

        val togglePasswordVisibility1 = findViewById<ImageView>(R.id.mostrarContraActualSoli)
        val togglePasswordVisibility2 = findViewById<ImageView>(R.id.mostrarContraNuevaSoli)
        val togglePasswordVisibility3 = findViewById<ImageView>(R.id.mostrarContraNuevaIngreOtravezSoli)

        togglePasswordVisibility1.setOnClickListener {
            if (isPassword1Visible) {
                // Ocultar contraseña
                passwordview1.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility1.setImageResource(R.drawable.nuevacontra)
            } else {
                // Mostrar contraseña
                passwordview1.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePasswordVisibility1.setImageResource(R.drawable.mostrarcontrasena)
            }
            isPassword1Visible = !isPassword1Visible
            passwordview1.setSelection(passwordview1.text.length)
        }

        togglePasswordVisibility2.setOnClickListener {
            if (isPassword2Visible) {
                // Ocultar contraseña
                passwordview2.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility2.setImageResource(R.drawable.nuevacontra)
            } else {
                // Mostrar contraseña
                passwordview2.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePasswordVisibility2.setImageResource(R.drawable.mostrarcontrasena)
            }
            isPassword2Visible = !isPassword2Visible
            passwordview2.setSelection(passwordview2.text.length)
        }

        togglePasswordVisibility3.setOnClickListener {
            if (isPassword3Visible) {
                // Ocultar contraseña
                passwordview3.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility3.setImageResource(R.drawable.nuevacontra)
            } else {
                // Mostrar contraseña
                passwordview3.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePasswordVisibility3.setImageResource(R.drawable.mostrarcontrasena)
            }
            isPassword3Visible = !isPassword3Visible
            passwordview3.setSelection(passwordview3.text.length)
        }


        // Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnSalir1.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        btnEditarContrasena.setOnClickListener {
            val contrasenaActual = txtContrasenaActual.text.toString().trim()
            val nuevaContrasena = txtNuevaContrasena.text.toString().trim()
            val nuevaContrasena2 = txtReIngresarNuevaContrasenaEmpleador.text.toString().trim()

            val IdSolicitante = login.IdSolicitante
            val verificarContraseña =
                Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

            if (contrasenaActual.isEmpty() || nuevaContrasena.isEmpty() || nuevaContrasena2.isEmpty()) {
                Toast.makeText(
                    this@cambiar_contrasena_Solicitante,
                    "No dejar campos vacíos",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarContraseña.matches(nuevaContrasena)) {
                Toast.makeText(
                    this@cambiar_contrasena_Solicitante,
                    "La nueva contraseña debe tener al menos 6 caracteres y contener un número o símbolo",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (nuevaContrasena != nuevaContrasena2) {
                Toast.makeText(
                    this@cambiar_contrasena_Solicitante,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val comprobarContrasena = objConexion?.prepareStatement(
                            "SELECT Contrasena FROM SOLICITANTE WHERE IdSolicitante = ?"
                        )
                        comprobarContrasena?.setString(1, IdSolicitante)

                        val contrasenaCoincide = comprobarContrasena?.executeQuery()

                        if (contrasenaCoincide?.next() == true) {
                            val contrasenaEnBD = contrasenaCoincide.getString("Contrasena")

                            if (hashSHA256(contrasenaActual) == contrasenaEnBD) {
                                // Encripto la contraseña usando la función de encriptación
                                val contrasenaEncriptada =
                                    hashSHA256(nuevaContrasena)

                                val actualizarContrasena = objConexion.prepareStatement(
                                    "UPDATE SOLICITANTE SET Contrasena = ? WHERE IdSolicitante = ?"
                                )
                                actualizarContrasena.setString(1, contrasenaEncriptada)
                                actualizarContrasena.setString(2, IdSolicitante)
                                actualizarContrasena.executeUpdate()

                                withContext(Dispatchers.Main) {
                                    AlertDialog.Builder(this@cambiar_contrasena_Solicitante)
                                        .setTitle("Contraseña actualizada")
                                        .setMessage("Tu contraseña ha sido actualizada correctamente, se cerrará la sesión para que vuelvas a ingresar.")
                                        .setPositiveButton("Aceptar") { _, _ ->
                                            // Cerrar la actividad actual
                                            finish()
                                            // Iniciar la actividad de login
                                            val login = Intent(this@cambiar_contrasena_Solicitante, login::class.java)
                                            login.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(login)
                                        }
                                        .show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@cambiar_contrasena_Solicitante,
                                        "La contraseña actual no es correcta",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }  catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@cambiar_contrasena_Solicitante,
                                "Error al cambiar la contraseña: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}