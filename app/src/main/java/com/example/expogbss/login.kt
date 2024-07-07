package com.example.expogbss

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtCorreoLogin = findViewById<EditText>(R.id.txtEmailLogin)
        val txtcontrasenaLogin = findViewById<EditText>(R.id.txtPasswordLogin)
        val btnSignIn = findViewById<ImageButton>(R.id.btnSignInLogin)

        val validarCorreo = Regex ("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

        val txtforgotPassword = findViewById<TextView>(R.id.txtForgotYourPassword)
        val txtRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)

        txtforgotPassword.setOnClickListener {
            //Cambio de pantalla a activity forgot password
            val pantallaOlvideContrasena = Intent(this, forgotPassword::class.java)
            startActivity(pantallaOlvideContrasena)
        }

        txtRegistrarse.setOnClickListener {
            //Cambio de pantalla para poder registrarse
            val pantallaRegistrarse = Intent(this, rolSelector::class.java)
            startActivity(pantallaRegistrarse)
        }

        fun hashSHA256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }


        //Botones para ingresar al sistema

        btnSignIn.setOnClickListener {

            val correo = txtCorreoLogin.text.toString()
            val contrasena = txtcontrasenaLogin.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(
                    this,
                    "No dejar espacios en blanco.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!validarCorreo.matches(correo)) {
                Toast.makeText(
                    this,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val pantallaPrincipal = Intent(this, ingresarCorreoRecupContrasena::class.java)

                CoroutineScope(Dispatchers.IO).launch {

                    val objConexion = ClaseConexion().cadenaConexion()

                    val contrasenaEncriptada = hashSHA256(txtcontrasenaLogin.text.toString())

                    val comprobarCredencialesSiEsEmpresa =
                        objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ? AND Contrasena = ?")!!
                    comprobarCredencialesSiEsEmpresa.setString(1, correo)
                    comprobarCredencialesSiEsEmpresa.setString(2, contrasenaEncriptada)

                    val comprobarCredencialesSiEsSolicitante =
                        objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ? AND Contrasena = ?")!!
                    comprobarCredencialesSiEsSolicitante.setString(1, correo)
                    comprobarCredencialesSiEsSolicitante.setString(2, contrasenaEncriptada)

                    val esEmpresa = comprobarCredencialesSiEsEmpresa.executeQuery()
                    val esSolicitante = comprobarCredencialesSiEsSolicitante.executeQuery()


                    if (esEmpresa.next()) {
                        startActivity(pantallaPrincipal)
                    } else if (
                        esSolicitante.next()) {
                        startActivity(pantallaPrincipal)
                    }else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@login, "Usuario no encontrado, verifique las credenciales", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
