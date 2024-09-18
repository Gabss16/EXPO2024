package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class perfilEmpresa : Fragment() {

    private lateinit var textViewNombreEmpresa: TextView
    private lateinit var textViewCorreoEmpresa: TextView
    private lateinit var textViewNombreEmpleador: TextView
    private lateinit var textViewNumeroEmpleador: TextView
    private lateinit var textViewDireccionEmpleador: TextView
    private lateinit var textViewSitioWeb: TextView
    private lateinit var imgFotoEmpleador: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_perfil_empresa, container, false)

        // Inicializar vistas
        textViewNombreEmpresa = root.findViewById(R.id.textViewNombreEmpresa)
        textViewCorreoEmpresa = root.findViewById(R.id.textViewCorreoEmpresa)
        textViewNombreEmpleador = root.findViewById(R.id.textViewNombreEmpleador)
        textViewNumeroEmpleador = root.findViewById(R.id.textViewNumeroEmpleador)
        textViewDireccionEmpleador = root.findViewById(R.id.textViewDireccionEmpleador)
        textViewSitioWeb = root.findViewById(R.id.textViewSitioWeb)
        imgFotoEmpleador = root.findViewById(R.id.imgFotoEmpleador)


        val btnEditarPerfil = root.findViewById<ImageView>(R.id.btnEditarPerfil)

        // Manejar el evento de clic en el botón de editar perfil
        btnEditarPerfil.setOnClickListener {
            // Iniciar la actividad "editar_perfil_Empleador"
            val intent = Intent(activity, editar_perfil_Empleador::class.java)
            startActivity(intent)
        }

//        val btnCerrarSesionEmpleador = root.findViewById<EditText>(R.id.btnCerrarSesionEmpleador)
//        // Manejar el evento de clic en el botón de editar perfil
//        btnCerrarSesionEmpleador.setOnClickListener {
//            // Iniciar la actividad "editar_perfil_Empleador"
//            val intent = Intent(activity, login::class.java)
//            startActivity(intent)
//        }

        val btnEditarContrasena = root.findViewById<ImageView>(R.id.btnEditarContrasenaEmpresa)

        // Manejar el evento de clic en el botón de editar contraseña
        btnEditarContrasena.setOnClickListener {
            // Iniciar la actividad "Cambiar_ContrasenaEmpleador"
            val intent = Intent(activity, Cambiar_ContrasenaEmpleador::class.java)
            startActivity(intent)
        }
        // Cargar datos del perfil

        cargarDatosPerfil()
        return root


    }
    private fun cargarDatosPerfil() {
        // Aquí cargas los datos del perfil desde tus variables o base de datos
        textViewNombreEmpresa.text = login.nombreEmpresa
        textViewCorreoEmpresa.text = login.correoEmpleador
        textViewNombreEmpleador.text = login.nombreEmpleador
        textViewNumeroEmpleador.text = login.numeroEmpleador
        textViewDireccionEmpleador.text = login.direccionEmpleador
        textViewSitioWeb.text = login.sitioWebEmpleador

        // Cargar la imagen con Glide
        Glide.with(this).load(login.fotoEmpleador).into(imgFotoEmpleador)
    }

}