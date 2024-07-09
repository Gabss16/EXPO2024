package com.example.expogbss

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import modelo.ClaseConexion

class cambio_de_contrasena : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cambio_de_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        
        val verificarContraseña =
            Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")



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
}