package com.example.expogbss

import android.annotation.SuppressLint
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class ingresarCorreoRecupContrasena : AppCompatActivity() {


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ingresar_correo_recup_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //TODO: Verificar en clase por qué no se envían los correos, el código es el correcto

        val btnEnviar = findViewById<Button>(R.id.btnEnviarCodigoRecuperacion)
        val txtcorreoRecuperacion = findViewById<EditText>(R.id.txtIngresarCorreoRecuperacion)
        val codigoRecupContrasena = (1000..9999).random()


            btnEnviar.setOnClickListener {

                val correo = txtcorreoRecuperacion.text.toString()

                //Validar que el Texview de correo no esté vacío
                if (correo.isEmpty())
                {
                    Toast.makeText(
                        this,
                        "No dejar espacios en blanco.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    CoroutineScope(Dispatchers.IO).launch {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val correoRecuperacion = txtcorreoRecuperacion.text.toString()


                        //Se ejecutan los select respectivos para verificar que el correo exista en alguna de las tablas existentes
                        val comprobarCredencialesSiEsSolicitante =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ?")!!
                        comprobarCredencialesSiEsSolicitante.setString(1, correoRecuperacion)

                        val comprobarCredencialesSiEsEmpleador =
                            objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ?")!!
                        comprobarCredencialesSiEsEmpleador.setString(1, correoRecuperacion)

                        val esEmpleador = comprobarCredencialesSiEsEmpleador.executeQuery()
                        val esSolicitante = comprobarCredencialesSiEsSolicitante.executeQuery()

                        //Bloque de código que se ejecuta en caso de detectar el correo en la tabla de Empleador
                        if (esEmpleador.next()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                println("Es empleador kkk")
                                val correoEnviado = recuperarContrasena(
                                    correoRecuperacion,"Recuperación de contraseña","Hola este es su codigo de recuperacion: $codigoRecupContrasena")
                                if (correoEnviado) {
                                    val pantallaIngresoCodigo = Intent(
                                        this@ingresarCorreoRecupContrasena,recoveryCode::class.java).apply {
                                        putExtra("codigoRecuperacion", codigoRecupContrasena)
                                        putExtra("correo", correoRecuperacion)
                                    }
                                    startActivity(pantallaIngresoCodigo)
                                    finish()
                                } else {
                                    println("PapuError")
                                }
                            }
                        }
                        else if (esSolicitante.next()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                println("entra a la corrutina")
                                val correoEnviado = recuperarContrasena(
                                    correoRecuperacion,
                                    "Recuperación de contraseña",
                                    "Hola este es su codigo de recuperacion: $codigoRecupContrasena"
                                )

                                if (correoEnviado) {
                                    val pantallaIngresoCodigo = Intent(
                                        this@ingresarCorreoRecupContrasena,
                                        recoveryCode::class.java
                                    ).apply {
                                        putExtra("codigoRecuperacion", codigoRecupContrasena)
                                        putExtra("correo", correoRecuperacion)
                                    }
                                    startActivity(pantallaIngresoCodigo)
                                    finish()
                                } else {
                                    println("PapuError")
                                }
                            }
                        } else {
                            //Toast para indicar a usuario que el correo no existe
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@ingresarCorreoRecupContrasena, "Correo electrónico no encontrado, ingrese un correo válido", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
        }
    }
}
