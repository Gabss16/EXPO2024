package com.example.expogbss

import android.content.Intent
import android.view.KeyEvent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import java.security.MessageDigest

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

        //Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        //Botones
        val btnConfirmarCodigo = findViewById<Button>(R.id.btnConfirmarCodigo)
        val btnReenviarCodigo = findViewById<Button>(R.id.btnVolverAEnviarCodigo)


        //Códigos que se traen de la pantalla anterior
        val correo = ingresarCorreoRecupContrasena.correoIngresado
        val codigoRecuperacion = ingresarCorreoRecupContrasena.codigo

        //Declaro los 4 dígitos
        val txtPrimerDigito = findViewById<EditText>(R.id.txtPrimerDigito)
        val txtSegundoDigito = findViewById<EditText>(R.id.txtSegundoDigito)
        val txtTercerDigito = findViewById<EditText>(R.id.txtTercerDigito)
        val txtCuartoDigito = findViewById<EditText>(R.id.txtCuartoDigito)
        val primerDigito = txtPrimerDigito.text.toString()
        val segundoDigito = txtSegundoDigito.text.toString()
        val tercerDigito = txtTercerDigito.text.toString()
        val cuartoDigito = txtCuartoDigito.text.toString()

     println("Codigo recibido $codigoRecuperacion")

        // Función para cambiar el foco al siguiente EditText
        fun setupNextEditText(currentEditText: EditText, nextEditText: EditText) {
            currentEditText.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return@setOnKeyListener false
                }
                if (event.action == KeyEvent.ACTION_UP && keyCode != KeyEvent.KEYCODE_DEL) {
                    if (currentEditText.text.length == 1) {
                        nextEditText.requestFocus()
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }

        // Configurar el cambio de foco para cada EditText
        setupNextEditText(txtPrimerDigito, txtSegundoDigito)
        setupNextEditText(txtSegundoDigito, txtTercerDigito)
        setupNextEditText(txtTercerDigito, txtCuartoDigito)

        btnConfirmarCodigo.setOnClickListener {
            val primerDigito = txtPrimerDigito.text.toString()
            val segundoDigito = txtSegundoDigito.text.toString()
            val tercerDigito = txtTercerDigito.text.toString()
            val cuartoDigito = txtCuartoDigito.text.toString()
            val codigoIngresado = "$primerDigito$segundoDigito$tercerDigito$cuartoDigito"
            println("Código ingresado: $codigoIngresado")

            if (primerDigito.isEmpty() || segundoDigito.isEmpty() || tercerDigito.isEmpty() || cuartoDigito.isEmpty()) {
                Toast.makeText(
                    this@recoveryCode,
                    "Por favor, ingresa un código válido",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (codigoIngresado == codigoRecuperacion) {
                val verificarContraseña =
                    Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

                // Mostrar AlertDialog para ingresar nueva contraseña
                val builder = AlertDialog.Builder(this@recoveryCode)
                val inflater = layoutInflater
                val dialogLayout = inflater.inflate(R.layout.alertdialogcambiodecontrasena, null)
                val editTextNewPassword =
                    dialogLayout.findViewById<EditText>(R.id.txtnuevacontrasena)

                builder.setTitle("Nueva Contraseña")
                    .setMessage("Ingrese su nueva contraseña")
                    .setView(dialogLayout)
                    .setPositiveButton(
                        "Confirmar",
                        null
                    ) // Pasamos null para manejar el clic manualmente
                    .create()

                val alertDialog = builder.create()
                alertDialog.show()

                // Manejo manual del botón Confirmar
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    val newPassword = editTextNewPassword.text.toString()

                    if (newPassword.isEmpty()) {
                        Toast.makeText(
                            this@recoveryCode,
                            "La contraseña no puede estar vacía",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!verificarContraseña.matches(newPassword)) {
                        Toast.makeText(
                            this@recoveryCode,
                            "La contraseña debe tener al menos 6 caracteres y un carácter especial",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val objConexion = ClaseConexion().cadenaConexion()

                        // Encripto la contraseña usando la función de encriptación
                        val contrasenaEncriptada = hashSHA256(newPassword)

                        val actualizarcontraseña =
                            objConexion?.prepareStatement("UPDATE EMPLEADOR SET contrasena = ? WHERE CorreoElectronico = ?")!!
                        actualizarcontraseña.setString(1, contrasenaEncriptada)
                        actualizarcontraseña.setString(2, correo)
                        actualizarcontraseña.executeQuery()

                        // Si todo está bien, cerrar el AlertDialog
                        alertDialog.dismiss()
                    }
                }
            } else {
                Toast.makeText(
                    this@recoveryCode,
                    "El código ingresado es incorrecto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }





        btnReenviarCodigo.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
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