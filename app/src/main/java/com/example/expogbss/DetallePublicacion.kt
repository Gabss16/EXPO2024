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

        val idTrabajo = intent.getIntExtra("IdTrabajo", 1234)

//TODO DEFINIR BIEN QUÉ MÁS SE VA A MOSTRAR EN LA SOLICITUD

        fun obtenerSolicitudesParaTrabajo(idTrabajo: Int): List<Solicitud> {
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.prepareStatement("""
     SELECT
                s.IdSolicitud,
                s.IdSolicitante,
                ss.Nombre as NombreSolicitante,
                s.IdTrabajo,
                s.FechaSolicitud,
                s.Estado,
                t.Titulo AS TituloTrabajo,
                ss.IdAreaDeTrabajo,
                A.NombreAreaDetrabajo AS CategoriaTrabajoSolicitante
            FROM SOLICITUD s
            INNER JOIN TRABAJO t ON s.IdTrabajo = t.IdTrabajo
                INNER JOIN SOLICITANTE ss ON s.IdSolicitante = ss.IdSolicitante
                INNER JOIN AreaDeTrabajo A ON ss.IdAreaDeTrabajo = A.IdAreaDeTrabajo
        WHERE s.Estado = 'Pendiente' AND s.IdTrabajo = ?
    """)

            // Establecer el idTrabajo en la consulta
            statement?.setInt(1, idTrabajo)

            val resultSet = statement?.executeQuery()

            val listaSolicitud = mutableListOf<Solicitud>()

            while (resultSet?.next() == true) {
                val idSolicitud = resultSet.getInt("IdSolicitud")
                val idSolicitante = resultSet.getString("IdSolicitante")
                val idTrabajoDb = resultSet.getInt("IdTrabajo")
                val fechaSolicitud = resultSet.getString("FechaSolicitud")
                val estado = resultSet.getString("Estado")
                val tituloTrabajo = resultSet.getString("TituloTrabajo")
                val categoriaTrabajo = resultSet.getString("CategoriaTrabajoSolicitante")
                val nombreSolicitante = resultSet.getString("NombreSolicitante")

                val solicitud = Solicitud(
                    idSolicitud,
                    idSolicitante,
                    idTrabajoDb,
                    fechaSolicitud,
                    estado,
                    tituloTrabajo,
                    categoriaTrabajo,
                    nombreSolicitante
                )

                listaSolicitud.add(solicitud)
            }

            return listaSolicitud
        }

        // Configurar adaptador para solicitudes
        CoroutineScope(Dispatchers.IO).launch {
            val solicitudesDb = obtenerSolicitudesParaTrabajo(idTrabajo)
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorSolicitud(solicitudesDb)
                rcvSolicitudes.adapter = adapter
            }
        }




    }
}