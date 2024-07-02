package com.example.expogbss

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.security.MessageDigest
import java.util.UUID

class registro_empresa : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_empresa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1-Mandar a llamar a todos los elementos de la vista
        val txtNombreEmpleador = findViewById<EditText>(R.id.txtNombreEmpleador)
        val txtEmpresaEmpleador = findViewById<EditText>(R.id.txtNombreEmpresaEmpleador)
        val txtCorreoEmpleador = findViewById<EditText>(R.id.txtCorreoEmpleador)
        val txtContrasenaEmpleador = findViewById<EditText>(R.id.txtContrasenaEmpleador)
        val txtTelefoEmpleador = findViewById<EditText>(R.id.txtTelefonoEmpleador)
        val txtDireccionEmpleador = findViewById<EditText>(R.id.txtDireccionEmpleador)
        val txtSitioWebEmpleador = findViewById<EditText>(R.id.txtSitioWebEmpleador)
        val spDepartamentos = findViewById<Spinner>(R.id.spDepartamento)


        val listadoDepartamentos = arrayOf("Ahuachapán",
            "Cabañas",
            "Chalatenango",
            "Cuscatlán",
            "La Libertad",
            "Morazán",
            "La Paz",
            "Santa Ana",
            "San Miguel",
            "San Vicente",
            "San Salvador",
            "Sonsonate"
            ,"La Unión",
            "Usulután")


        val adaptadorDeLinea = ArrayAdapter (this, android.R.layout.simple_spinner_dropdown_item,listadoDepartamentos)

        spDepartamentos.adapter =adaptadorDeLinea

        val btnCrearCuentaEmpleador = findViewById<ImageView>(R.id.btnCrearCuentaEmpleador)

        //Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnCrearCuentaEmpleador.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                //Creo un objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //Encripto la contraseña usando la función de arriba
                val contrasenaEncriptada = hashSHA256(txtContrasenaEmpleador.text.toString())

                //Creo una variable que contenga un PrepareStatement
                val crearUsuario =
                    objConexion?.prepareStatement("INSERT INTO EMPLEADOR (IdEmpleador, NombreEmpresa, CorreoElectronico, NumeroTelefono,Direccion,SitioWeb, NombreRepresentante, Ciudad, Contrasena) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )")!!
                crearUsuario.setString(1, UUID.randomUUID().toString())
                crearUsuario.setString(2, txtEmpresaEmpleador.text.toString())
                crearUsuario.setString(3, txtCorreoEmpleador.text.toString())
                crearUsuario.setString(4, txtTelefoEmpleador.text.toString())
                crearUsuario.setString(5, txtDireccionEmpleador.text.toString())
                crearUsuario.setString(6, txtSitioWebEmpleador.text.toString())
                crearUsuario.setString(7, txtNombreEmpleador.text.toString())
                crearUsuario.setString(8, "hola")
                crearUsuario.setString(9, contrasenaEncriptada)
                crearUsuario.executeUpdate()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@registro_empresa, "Usuario creado", Toast.LENGTH_SHORT).show()
                    txtNombreEmpleador.setText("")
                    txtEmpresaEmpleador.setText("")
                    txtCorreoEmpleador.setText("")
                    txtContrasenaEmpleador.setText("")
                    txtTelefoEmpleador.setText("")
                    txtDireccionEmpleador.setText("")
                    txtSitioWebEmpleador.setText("")
                }
            }
    }
}
}