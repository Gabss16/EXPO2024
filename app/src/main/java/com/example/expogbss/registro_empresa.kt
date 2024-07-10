package com.example.expogbss

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.content.Intent
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import android.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
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

        //TODO: Faltan los inserts en caso de no agregar sitio web o nombre de empresa.
        //TODO: Ver cómo evitar que exista el mismo correo en ambas tablas, consultar con el profe

        //1-Mandar a llamar a todos los elementos de la vista
        val txtNombreEmpleador = findViewById<EditText>(R.id.txtNombreEmpleador)
        val txtEmpresaEmpleador = findViewById<EditText>(R.id.txtNombreEmpresaEmpleador)
        val txtCorreoEmpleador = findViewById<EditText>(R.id.txtCorreoEmpleador)
        val txtContrasenaEmpleador = findViewById<EditText>(R.id.txtContrasenaEmpleador)
        val txtTelefonoEmpleador = findViewById<EditText>(R.id.txtTelefonoEmpleador)
        val txtDireccionEmpleador = findViewById<EditText>(R.id.txtDireccionEmpleador)
        val txtSitioWebEmpleador = findViewById<EditText>(R.id.txtSitioWebEmpleador)
        val spDepartamentos = findViewById<Spinner>(R.id.spDepartamento)

        val listadoDepartamentos = listOf(
            "Ahuachapán",
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
            "Sonsonate",
            "La Unión",
            "Usulután"
        )
        val adaptadorDeLinea =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoDepartamentos)
        spDepartamentos.adapter = adaptadorDeLinea

        val btnCrearCuentaEmpleador = findViewById<ImageView>(R.id.btnCrearCuentaEmpleador)

        //Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        //Código para registrar a un empleador

        btnCrearCuentaEmpleador.setOnClickListener {

            //mando a llamar a cada textview
            val nombreEmpleador = txtNombreEmpleador.text.toString()
            val CorreoEmpleador = txtCorreoEmpleador.text.toString()
            val ContrasenaEmpleador = txtContrasenaEmpleador.text.toString()
            val TelefoEmpleador = txtTelefonoEmpleador.text.toString()
            val DireccionEmpleador = txtDireccionEmpleador.text.toString()
            val SitioWebEmpleador = txtSitioWebEmpleador.text.toString()
            val EmpresaEmpleador = txtEmpresaEmpleador.text.toString()

            val VerificarTelefono = Regex("^\\d{4}-\\d{4}\$")
            val verificarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val verificarContraseña =
                Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

            //Validaciones de campos vacíos y cosas por ese estilo
            if (nombreEmpleador.isEmpty() || EmpresaEmpleador.isEmpty() || CorreoEmpleador.isEmpty() || ContrasenaEmpleador.isEmpty() || TelefoEmpleador.isEmpty() || DireccionEmpleador.isEmpty()) {
                Toast.makeText(
                    this@registro_empresa,
                    "Por favor, llenar los espacios obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!VerificarTelefono.matches(TelefoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un número de teléfono válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarCorreo.matches(CorreoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarContraseña.matches(ContrasenaEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "La contraseña debe contener al menos un caracter especial y tener más de 6 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {

                    val objConexion = ClaseConexion().cadenaConexion()

                    //Encripto la contraseña usando la función de encriptación
                    val contrasenaEncriptada = hashSHA256(txtContrasenaEmpleador.text.toString())

                    //Creo una variable que contenga un PrepareStatement

                    val crearUsuario =
                        objConexion?.prepareStatement("INSERT INTO EMPLEADOR (IdEmpleador, NombreEmpresa, CorreoElectronico, NumeroTelefono,Direccion,SitioWeb, NombreRepresentante, Departamento, Contrasena,Estado, Foto) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )")!!
                    crearUsuario.setString(1, uuid)
                    crearUsuario.setString(2, txtEmpresaEmpleador.text.toString())
                    crearUsuario.setString(3, txtCorreoEmpleador.text.toString())
                    crearUsuario.setString(4, txtTelefonoEmpleador.text.toString())
                    crearUsuario.setString(5, txtDireccionEmpleador.text.toString())
                    crearUsuario.setString(6, txtSitioWebEmpleador.text.toString())
                    crearUsuario.setString(7, txtNombreEmpleador.text.toString())
                    crearUsuario.setString(8, spDepartamentos.selectedItem.toString())
                    crearUsuario.setString(9, contrasenaEncriptada)
                    crearUsuario.setString(10, "Pendiente")
                    crearUsuario.setString(11,uuid )

                    crearUsuario.executeUpdate()

                    withContext(Dispatchers.Main) {
                        AlertDialog.Builder(this@registro_empresa)
                            .setTitle("Cuenta registrada")
                            .setMessage("Tu cuenta ha sido creada, puedes regresar al inicio de sesión.")
                            .setPositiveButton("Aceptar", null)
                            .show()
                        txtNombreEmpleador.setText("")
                        txtEmpresaEmpleador.setText("")
                        txtCorreoEmpleador.setText("")
                        txtContrasenaEmpleador.setText("")
                        txtTelefonoEmpleador.setText("")
                        txtDireccionEmpleador.setText("")
                        txtSitioWebEmpleador.setText("")
                    }
                }
            }
        }
    }
}