package com.example.expogbss

import android.content.Intent
import android.view.KeyEvent
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.expogbss.ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.codigo
import com.example.expogbss.ingresarCorreoRecupContrasena.variablesGlobalesRecuperacionDeContrasena.correoIngresado
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class recoveryCode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recovery_code)
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

        //Botones
        val btnConfirmarCodigo = findViewById<Button>(R.id.btnConfirmarCodigo)
        val btnReenviarCodigo = findViewById<Button>(R.id.btnVolverAEnviarCodigo)

        //Códigos que se traen de la pantalla anterior
        val correo = correoIngresado
        val codigoRecuperacion = codigo

        //Declaro los 4 dígitos
        val txtPrimerDigito = findViewById<EditText>(R.id.txtPrimerDigito)
        val txtSegundoDigito = findViewById<EditText>(R.id.txtSegundoDigito)
        val txtTercerDigito = findViewById<EditText>(R.id.txtTercerDigito)
        val txtCuartoDigito = findViewById<EditText>(R.id.txtCuartoDigito)

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
            try {
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
                    val pantallaCambioContrasena = Intent(
                        this@recoveryCode, cambio_de_contrasena::class.java
                    )
                    startActivity(pantallaCambioContrasena)
                    finish()
                } else {
                    Toast.makeText(
                        this@recoveryCode,
                        "El código ingresado es incorrecto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@recoveryCode,
                    "Ha ocurrido un error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }

        btnReenviarCodigo.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val correoConHtml = generarHTMLCorreo(codigoRecuperacion)

                    val correoEnviado = recuperarContrasena(
                        correo,
                        "Recuperación de contraseña", correoConHtml)

                    withContext(Dispatchers.Main) {
                        if (correoEnviado) {
                            Toast.makeText(
                                this@recoveryCode,
                                "Correo Reenviado satisfactoriamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@recoveryCode,
                                "Hubo un error al enviar el correo, intenta de nuevo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@recoveryCode,
                            "Ha ocurrido un error al reenviar el correo: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    e.printStackTrace()
                }
            }
        }
    }
}
