package com.example.expogbss

import RecicleViewHelpers.AdaptadorSolicitud
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DetallePublicacion : AppCompatActivity() {

    private lateinit var txtTituloDetalle: TextView
    private lateinit var txtDescripcionDetalle: TextView
    private lateinit var rcvSolicitudes: RecyclerView
    private lateinit var solicitudesAdapter: AdaptadorSolicitud

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_publicacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Views
        txtTituloDetalle = findViewById(R.id.txtJobTitle1)
        txtDescripcionDetalle = findViewById(R.id.txtJobDescription)
        rcvSolicitudes = findViewById(R.id.rcvSolicitudesRecibidas)

        // Obtener datos del Intent
        val titulo = intent.getStringExtra("Titulo")
        val descripcion = intent.getStringExtra("Descripcion")

        // Establecer datos en las Views
        txtTituloDetalle.text = titulo
        txtDescripcionDetalle.text = descripcion

        // Configurar RecyclerView para solicitudes
        rcvSolicitudes.layoutManager = LinearLayoutManager(this)

        // Obtener lista de solicitudes (esto es un ejemplo; debes obtenerlas desde tu fuente de datos)
        //val solicitudes = obtenerSolicitudesParaTrabajo()

        // Configurar adaptador para solicitudes
       // solicitudesAdapter = AdaptadorSolicitud(solicitudes)
        rcvSolicitudes.adapter = solicitudesAdapter


    }
}