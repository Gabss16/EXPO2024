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
        lateinit var IdSolicitante: String
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
            //obtener idEmpleador
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()

                    // Preparar la consulta para obtener IdEmpleador
                    val resultSet =
                        objConexion?.prepareStatement("SELECT IdEmpleador FROM EMPLEADOR WHERE CorreoElectronico = ?")
                    resultSet?.setString(1, correoLogin)

                    // Ejecutar la consulta y obtener el resultado
                    val resultado = resultSet?.executeQuery()

                    // Verificar si se encontró un resultado
                    if (resultado?.next() == true) {
                        IdEmpleador = resultado.getString("IdEmpleador")
                        // Ahora IdEmpleador tiene el valor obtenido de la base de datos
                    } else {
                        // Manejar caso donde no se encontró IdEmpleador (correo no existe)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@login, "Correo no encontrado", Toast.LENGTH_SHORT)
                                .show()
                        }
                        return@launch  // Salir del bloque de código si no se encontró el correo
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@login,
                            "Error al consultar la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            //obtener idSolicitante
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()

                    // Preparar la consulta para obtener IdSolicitante
                    val resultSetSolicitante =
                        objConexion?.prepareStatement("SELECT IdSolicitante FROM SOLICITANTE WHERE CorreoElectronico = ?")
                    resultSetSolicitante?.setString(1, correoLogin)

                    // Ejecutar la consulta y obtener el resultado
                    val resultadoSolicitante = resultSetSolicitante?.executeQuery()

                    // Verificar si se encontró un resultado
                    if (resultadoSolicitante?.next() == true) {
                        IdSolicitante = resultadoSolicitante.getString("IdSolicitante")
                    } else {
                        IdSolicitante = ""
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@login,
                            "Error al consultar la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            val correo = txtCorreoLogin.text.toString()
            val contrasena = txtcontrasenaLogin.text.toString()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "No dejar espacios en blanco.", Toast.LENGTH_SHORT).show()
            } else if (!validarCorreo.matches(correo)) {
                Toast.makeText(this, "Ingresar un correo electrónico válido.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val pantallaEmpleador = Intent(this, Empleadores::class.java)
                val pantallaSolicitante = Intent(this, solicitante::class.java)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()

                        val contrasenaEncriptada = hashSHA256(txtcontrasenaLogin.text.toString())

                        // Se ejecutan los select respectivos para verificar que el correo exista en alguna de las tablas existentes
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

                        // Si el usuario es Empleador, verificar el estado
                        if (esEmpleador.next()) {
                            val estadoEmpleador = esEmpleador.getString("Estado")
                            if (estadoEmpleador == "Activo") {
                                nombreEmpresa = esEmpleador.getString("NombreEmpresa") ?: ""
                                nombreEmpleador = esEmpleador.getString("NombreRepresentante")
                                correoEmpleador = esEmpleador.getString("CorreoElectronico")
                                numeroEmpleador = esEmpleador.getString("NumeroTelefono")
                                direccionEmpleador = esEmpleador.getString("Direccion")
                                sitioWebEmpleador = esEmpleador.getString("SitioWeb") ?: ""
                                fotoEmpleador = esEmpleador.getString("Foto")
                                withContext(Dispatchers.Main) {
                                    startActivity(pantallaEmpleador)
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@login,
                                        "Su cuenta aún está en revisión y no puede ingresar",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else if (esSolicitante.next()) {
                            // Si el usuario es Solicitante, se le muestra su pantalla respectiva
                            nombresSolicitante = esSolicitante.getString("Nombre")
                            correoSolicitante = esSolicitante.getString("CorreoElectronico")
                            numeroSolicitante = esSolicitante.getString("Telefono")
                            direccionSolicitante = esSolicitante.getString("Direccion")
                            departamentoSolicitante = esSolicitante.getString("Departamento")
                            fechaNacimiento = esSolicitante.getString("FechaDeNacimiento")
                            generoSolicitante = esSolicitante.getString("Genero")
                            areaDeTrabajo = esSolicitante.getString("AreaDeTrabajo")
                            habilidades = esSolicitante.getString("Habilidades")
                            fotoSolicitante = esSolicitante.getString("Foto")

                            withContext(Dispatchers.Main) {
                                startActivity(pantallaSolicitante)
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@login,
                                "Error al consultar la base de datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


    }
}

