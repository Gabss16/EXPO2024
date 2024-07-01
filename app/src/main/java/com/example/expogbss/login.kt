package com.example.expogbss

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion

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
        val txtEmail = findViewById<EditText>(R.id.txtEmailLogin)
        val txtPassword = findViewById<EditText>(R.id.txtPasswordLogin)
        val btnSignIn = findViewById<ImageButton>(R.id.btnSignInLogin)
        val txtforgotPassword = findViewById<TextView>(R.id.txtForgotYourPassword)
        //  val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse) (Agregar cuando esté el botón


        //Botones para ingresar al sistema

        btnSignIn.setOnClickListener {

            val pantallaPrincipal = Intent(this, solicitante::class.java)

            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()


                val comprobarCredencialesSiEsEmpresa =
                    objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ? AND Contrasena = ?")!!
                comprobarCredencialesSiEsEmpresa.setString(1, txtEmail.text.toString())
                comprobarCredencialesSiEsEmpresa.setString(2, txtPassword.text.toString())

                val comprobarCredencialesSiEsSolicitante =
                    objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ? AND Contrasena = ?")!!
                comprobarCredencialesSiEsSolicitante.setString(1, txtEmail.text.toString())
                comprobarCredencialesSiEsSolicitante.setString(2, txtPassword.text.toString())

                val esEmpresa = comprobarCredencialesSiEsEmpresa.executeQuery()
                val esSolicitante = comprobarCredencialesSiEsSolicitante.executeQuery()

                if (esEmpresa.next()) {
                    startActivity(pantallaPrincipal)
                }
                if(esSolicitante.next()){
                    startActivity(pantallaPrincipal)
                }
                else
                    {
                    println("Usuario no encontrado, verifique las credenciales")
                }
            }
        }
        txtforgotPassword.setOnClickListener {
            //Cambio de pantalla a activity forgot password
               val pantallaOlvideContrasena = Intent(this, forgotPassword::class.java)
                 startActivity(pantallaOlvideContrasena)
             }

//         Agregar cuando esté el botón de registrarse, y la activity de registrarse
//          btnRegistrarse.setOnClickListener {
//        Cambio de pantalla para poder registrarse
//           val pantallaRegistrarse = Intent(this, Registrarse::class.java)
//             startActivity(pantallaRegistrarse)
//           }
    }
}
