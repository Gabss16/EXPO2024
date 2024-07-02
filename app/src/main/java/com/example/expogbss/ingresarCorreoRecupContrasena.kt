package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ingresar_correo_recup_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnEnviar = findViewById<Button>(R.id.btnEnviarCodigoRecuperacion)
        val correoRecuperacion = findViewById<EditText>(R.id.txtIngresarCorreoRecuperacion)
        var codigoRecuperacion = (1000..9999).random()

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()


            val comprobarCredencialesSiEsEmpresa =
                objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ?")!!
            comprobarCredencialesSiEsEmpresa.setString(1, correoRecuperacion.text.toString())

            val comprobarCredencialesSiEsSolicitante =
                objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ?")!!
            comprobarCredencialesSiEsSolicitante.setString(1, correoRecuperacion.text.toString())

            val esEmpresa = comprobarCredencialesSiEsEmpresa.executeQuery()
            val esSolicitante = comprobarCredencialesSiEsSolicitante.executeQuery()

            if (esEmpresa.next()) {
                btnEnviar.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        val correoEnviado = recuperarContrasena(correoRecuperacion.text.toString(), "Recuperación de contraseña", "Hola este es su codigo de recuperacion: $codigoRecuperacion")

                        if (correoEnviado) {
                            val pantallaIngresoCodigo = Intent(this@ingresarCorreoRecupContrasena, recoveryCode::class.java).apply {
                                putExtra("codigoRecuperacion", codigoRecuperacion)
                                putExtra("correo", correoRecuperacion.text.toString())
                            }
                            startActivity(pantallaIngresoCodigo)
                            finish()
                        } else {
                            println("PapuError")
                        }
                    }
                }
            }
            if(esSolicitante.next()){
                btnEnviar.setOnClickListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        val correoEnviado = recuperarContrasena(correoRecuperacion.text.toString(), "Recuperación de contraseña", "Hola este es su codigo de recuperacion: $codigoRecuperacion")

                        if (correoEnviado) {
                            val pantallaIngresoCodigo = Intent(this@ingresarCorreoRecupContrasena, recoveryCode::class.java).apply {
                                putExtra("codigoRecuperacion", codigoRecuperacion)
                                putExtra("correo", correoRecuperacion.text.toString())
                            }
                            startActivity(pantallaIngresoCodigo)
                            finish()
                        } else {
                            println("PapuError")
                        }
                    }

                }            }
            else
            {
                println("Correo no encontrado, ingrese uno válido")
            }
        }
    }

    }
    }
