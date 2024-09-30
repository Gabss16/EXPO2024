package com.example.expogbss

import RecicleViewHelpers.AdaptadorPublicacion
import RecicleViewHelpers.AdaptadorSolicitud
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Solicitud
import modelo.Trabajo

class DetallePublicacion : AppCompatActivity() {

    private lateinit var txtTituloDetalle: TextView
    private lateinit var txtDescripcionDetalle: TextView
    private lateinit var rcvSolicitudes: RecyclerView
   // private lateinit var solicitudesAdapter: AdaptadorSolicitud

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
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

        val btnSalir = findViewById<ImageButton>(R.id.btnSalirDetallesPublicacion)

        btnSalir.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        // Obtener lista de solicitudes (esto es un ejemplo; debes obtenerlas desde tu fuente de datos)
        //val solicitudes = obtenerSolicitudesParaTrabajo()

        fun obtenerSolicitudesParaTrabajo(): List<Solicitud>{
            val objConexion = ClaseConexion().cadenaConexion()
            //2 - Creo un statement

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("""
SELECT
                s.IdSolicitud,
                s.IdSolicitante,
                s.IdTrabajo,
                s.FechaSolicitud,
                s.Estado,
                t.Titulo AS TituloTrabajo,
                t.IdAreaDeTrabajo AS CategoriaTrabajo
            FROM SOLICITUD s
            INNER JOIN TRABAJO t ON s.IdTrabajo = t.IdTrabajo
            WHERE s.Estado = 'Pendiente'""")!!

            //en esta variable se añaden TODOS los valores de mascotas
            val listaSolicitud = mutableListOf<Solicitud>()

            while (resultSet.next()) {
                val IdSolicitud = resultSet.getInt("IdSolicitud")
                val IdSolicitante = resultSet.getString("IdSolicitante")
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val FechaSolicitud = resultSet.getString("FechaSolicitud")
                val Estado = resultSet.getString("Estado")
                val tituloTrabajo = resultSet.getString("TituloTrabajo")
                val categoriaTrabajo = resultSet.getString("CategoriaTrabajo")


                val solicitud = Solicitud(
                    IdSolicitud,
                    IdSolicitante,
                    IdTrabajo,
                    FechaSolicitud,
                    Estado,
                    tituloTrabajo,
                    categoriaTrabajo
                )
                listaSolicitud.add(solicitud)
            }
            return listaSolicitud

        }

        // Configurar adaptador para solicitudes
        CoroutineScope(Dispatchers.IO).launch {
            val solicitudesDb = obtenerSolicitudesParaTrabajo()
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorSolicitud(solicitudesDb)
                rcvSolicitudes.adapter = adapter
            }
        }
       // solicitudesAdapter = AdaptadorSolicitud(solicitudes)
      //  rcvSolicitudes.adapter = solicitudesAdapter



    }
}