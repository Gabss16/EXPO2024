package com.example.expogbss

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

    companion object variablesGlobalesRecuperacionDeContrasena {
        lateinit var correoIngresado: String
        lateinit var codigo: String
        var CambiarContraEmpleador: Boolean = false
        var CambiarContraSolicitante: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ingresar_correo_recup_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun generarHTMLCorreo(codigo: String): String{
            return """
<html>
<body style="font-family: 'Roboto', sans-serif;
            background-color: #f5f7fa;
            margin: 0;
            padding: 0;">
    <div class="container" style="width: 100%;
            max-width: 600px; 
            margin: 50px auto;
            background-color: #ffffff;
            padding: 30px 20px;
            border-radius: 15px;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);">
        <div class="img" style="text-align: center;
            margin-top: 40px;">
            <img src="https://i.imgur.com/bXHJUmC.png" alt="Logo" width="400" style="border-radius: 10px;">
        </div>
        <div class="message" style="text-align: center;
            color: #2c3e50;
            margin-bottom: 40px;">
            <h2 style="font-size: 28px; 
            font-weight: 600;
            margin-bottom: 10px;">Recuperación de Contraseña</h2>
            <p style="font-size: 18px; color: #7f8c8d;">Usa el siguiente código para recuperar tu contraseña:</p>
            <div class="code" style="display: inline-block;
            padding: 20px 40px; 
            font-size: 26px; 
            color: #000;
            background-color: #d2dee7;
            border-radius: 10px; 
            margin-top: 20px;
            letter-spacing: 2px;">$codigo</div>
        </div>
        <div class="footer-logo" style="text-align: center;
            margin-top: 40px;">
            <img src="https://i.imgur.com/TU8KAcy.png" alt="Logo" width="550" style="border-radius: 10px;">
        </div>
    </div>
</body>
</html>
""".trimIndent()
        }

        val btnEnviar = findViewById<Button>(R.id.btnEnviarCodigoRecuperacion)
        val txtcorreoRecuperacion = findViewById<EditText>(R.id.txtIngresarCorreoRecuperacion)
        val codigoRecupContrasena = (1000..9999).random()

        val btnSalir3 = findViewById<ImageButton>(R.id.btnSalir3)

        btnSalir3.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        btnEnviar.setOnClickListener {
            val correo = txtcorreoRecuperacion.text.toString()

            //Validar que el Texview de correo no esté vacío
            if (correo.isEmpty()) {
                Toast.makeText(
                    this,
                    "No dejar espacios en blanco.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        correoIngresado = txtcorreoRecuperacion.text.toString()
                        codigo = codigoRecupContrasena.toString()

                        //Se ejecutan los select respectivos para verificar que el correo exista en alguna de las tablas existentes
                        val comprobarCredencialesSiEsSolicitante =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ?")!!
                        comprobarCredencialesSiEsSolicitante.setString(1, correoIngresado)

                        val comprobarCredencialesSiEsEmpleador =
                            objConexion?.prepareStatement("SELECT * FROM EMPLEADOR WHERE CorreoElectronico = ?")!!
                        comprobarCredencialesSiEsEmpleador.setString(1, correoIngresado)

                        val esEmpleador = comprobarCredencialesSiEsEmpleador.executeQuery()
                        val esSolicitante = comprobarCredencialesSiEsSolicitante.executeQuery()

                        //Bloque de código que se ejecuta en caso de detectar el correo en la tabla de Empleador
                        if (esEmpleador.next()) {
                            withContext(Dispatchers.Main) {
                                CambiarContraEmpleador = true
                                val correoConHtml = generarHTMLCorreo(codigo)

                                val correoEnviado = recuperarContrasena(
                                    correoIngresado,
                                    "Recuperación de contraseña", correoConHtml)
                                if (correoEnviado) {
                                    val pantallaIngresoCodigo = Intent(
                                        this@ingresarCorreoRecupContrasena, recoveryCode::class.java
                                    )
                                    startActivity(pantallaIngresoCodigo)
                                    finish()
                                } else {
                                    println("PapuError")
                                }
                            }
                        } else if (esSolicitante.next()) {
                            withContext(Dispatchers.Main) {
                                CambiarContraSolicitante = true
                                val correoConHtml = generarHTMLCorreo(codigo)

                                val correoEnviado = recuperarContrasena(
                                    correoIngresado,
                                    "Recuperación de contraseña",correoConHtml)

                                if (correoEnviado) {
                                    val pantallaIngresoCodigo = Intent(
                                        this@ingresarCorreoRecupContrasena,
                                        recoveryCode::class.java
                                    )
                                    startActivity(pantallaIngresoCodigo)
                                    finish()
                                } else {
                                    println("PapuError")
                                }
                            }
                        } else {
                            //Toast para indicar a usuario que el correo no existe
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@ingresarCorreoRecupContrasena,
                                    "Correo electrónico no encontrado, ingrese un correo válido",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@ingresarCorreoRecupContrasena,
                                "Ha ocurrido un error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
