package com.example.expogbss

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expogbss.ui.dashboard.DashboardFragment
import com.example.expogbss.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Empleador
import java.security.MessageDigest
import java.util.Date

class login : AppCompatActivity() {
    companion object variablesGlobalesRecuperacionDeContrasena {
        lateinit var correoLogin: String
        lateinit var IdEmpleador: String
        lateinit var nombresSolicitante: String
        lateinit var correoSolicitante: String
        lateinit var numeroSolicitante: String
        lateinit var direccionSolicitante: String
        lateinit var departamentoSolicitante: String
        lateinit var fechaNacimiento: String
        lateinit var generoSolicitante: String
        lateinit var areaDeTrabajo: String
        lateinit var habilidades: String
        lateinit var fotoSolicitante: String
        lateinit var nombreEmpresa: String
        lateinit var correoEmpleador: String
        lateinit var nombreEmpleador: String
        lateinit var numeroEmpleador: String
        lateinit var direccionEmpleador: String
        lateinit var sitioWebEmpleador: String
        lateinit var fotoEmpleador: String
        //TODO: Añadir todos los campos que quiero llamar en los perfiles
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val txtCorreoLogin = findViewById<EditText>(R.id.txtEmailLogin)
        val txtcontrasenaLogin = findViewById<EditText>(R.id.txtPasswordLogin)
        val btnSignIn = findViewById<ImageButton>(R.id.btnSignInLogin)

        val validarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")

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
            correoLogin = txtCorreoLogin.text.toString()
            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()
                val resultSet =
                    objConexion?.prepareStatement("SELECT IdEmpleador FROM EMPLEADOR WHERE CorreoElectronico = ?")!!
                resultSet.setString(1, correoLogin)
                val resultado = resultSet.executeQuery()
                IdEmpleador = resultado.toString()

            }


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
                val pantallaEmpleador = Intent(this, Empleadores::class.java)
                val pantallaSolicitante = Intent(this, solicitante::class.java)

                CoroutineScope(Dispatchers.IO).launch {

                    val objConexion = ClaseConexion().cadenaConexion()

                    val contrasenaEncriptada = hashSHA256(txtcontrasenaLogin.text.toString())


                    //Se ejecutan los select respectivos para verificar que el correo exista en alguna de las tablas existentes
                    val comprobarCredencialesSiEsEmpleador =
                        objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ? AND Contrasena = ?")!!
                    comprobarCredencialesSiEsEmpleador.setString(1, correo)
                    comprobarCredencialesSiEsEmpleador.setString(2, contrasenaEncriptada)

                    val comprobarCredencialesSiEsSolicitante =
                        objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ? AND Contrasena = ?")!!
                    comprobarCredencialesSiEsSolicitante.setString(1, correo)
                    comprobarCredencialesSiEsSolicitante.setString(2, contrasenaEncriptada)

                    val esEmpleador = comprobarCredencialesSiEsEmpleador.executeQuery()
                    val esSolicitante = comprobarCredencialesSiEsSolicitante.executeQuery()


                    //Si el usuario es Empleador, se le muestra su pantalla respectiva
                    if (esEmpleador.next()) {
                        nombreEmpresa = esEmpleador.getString("NombreEmpresa")
                        nombreEmpleador = esEmpleador.getString("NombreRepresentante")
                        correoEmpleador = esEmpleador.getString("CorreoElectronico")
                        numeroEmpleador = esEmpleador.getString("NumeroTelefono")
                        direccionEmpleador = esEmpleador.getString("Direccion")
                        sitioWebEmpleador = esEmpleador.getString("SitioWeb")
                        fotoEmpleador = esEmpleador.getString("Foto")
                        withContext(Dispatchers.Main) {
                            startActivity(pantallaEmpleador)
                        }
                    } else if (
                    //Si el usuario es Solicitante, se le muestra su pantalla respectiva
                        esSolicitante.next()) {
                        nombresSolicitante = esSolicitante.getString("Nombre")
                        correoSolicitante = esSolicitante.getString("CorreoElectronico")
                        numeroSolicitante = esSolicitante.getString("Telefono")
                        direccionSolicitante = esSolicitante.getString("Direccion")
                        departamentoSolicitante = esSolicitante.getString("Departamento")
                        fechaNacimiento = esSolicitante.getString("FechaDeNacimiento")
                        generoSolicitante = esSolicitante.getString("Genero")
                        areaDeTrabajo = esSolicitante.getString("IdAreaDeTrabajo")
                        habilidades = esSolicitante.getString("Habilidades")
                        fotoSolicitante = esSolicitante.getString("Foto")

                        startActivity(pantallaSolicitante)
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@login,
                                "Usuario no encontrado, verifique sus credenciales",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
