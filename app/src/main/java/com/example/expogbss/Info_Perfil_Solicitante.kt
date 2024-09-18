package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class Info_Perfil_Solicitante : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_perfil_solicitante)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textViewNombreSolicitante = findViewById<TextView>(R.id.textViewNombreSolicitante2)
        val textViewCorreoSolicitante = findViewById<TextView>(R.id.textViewCorreoSolicitante2)
        val textViewNumeroSolicitante = findViewById<TextView>(R.id.textViewNumeroSolicitante2)
        val textViewDireccionSolicitante = findViewById<TextView>(R.id.textViewDireccionSolicitante2)
        val textViewDepartamento = findViewById<TextView>(R.id.textViewDepartamento2)
        val textViewFechaSolicitante = findViewById<TextView>(R.id.textViewFechaSolicitante2)
        val textViewGeneroSolicitante = findViewById<TextView>(R.id.textViewGeneroSolicitante2)
        val textViewArea = findViewById<TextView>(R.id.textViewArea2)
        val textViewHabilidades = findViewById<TextView>(R.id.textViewHabilidades2)
        val btnSalir11 = findViewById<ImageButton>(R.id.salir11)
        val imgFotoSolicitante = findViewById<ImageView>(R.id.imgFotoSolicitante2)

        // Realiza la consulta en un hilo secundario usando corrutinas
        //val idSolicitante = intent.getIntExtra("IdSolicitante", -1)
        val idSolicitante = intent.getStringExtra("IdSolicitante")
        Log.d("Info_Perfil_Solicitante", "IdSolicitante recibido: $idSolicitante")

        btnSalir11.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().cadenaConexion()

                val query = """
            SELECT 
                s.Nombre, 
                s.CorreoElectronico, 
                s.Telefono, 
                s.Direccion, 
                d.Nombre AS NombreDepartamento, 
                s.FechaDeNacimiento, 
                s.Genero, 
                a.NombreAreaDeTrabajo, 
                s.Habilidades, 
                s.Foto 
            FROM 
                SOLICITANTE s 
            INNER JOIN 
                DEPARTAMENTO d ON s.IdDepartamento = d.IdDepartamento 
            INNER JOIN 
                AreaDeTrabajo a ON s.IdAreaDeTrabajo = a.IdAreaDeTrabajo 
            WHERE 
                s.IdSolicitante = ?
        """

                val statement = objConexion?.prepareStatement(query)

                statement?.setString(1, idSolicitante)
                val resultSet = statement?.executeQuery()

                if (resultSet?.next() == true) {
                    withContext(Dispatchers.Main) {
                        textViewNombreSolicitante.text = resultSet.getString("Nombre")
                        textViewCorreoSolicitante.text = resultSet.getString("CorreoElectronico")
                        textViewNumeroSolicitante.text = resultSet.getString("Telefono")
                        textViewDireccionSolicitante.text = resultSet.getString("Direccion")
                        textViewDepartamento.text = resultSet.getString("NombreDepartamento")
                        textViewFechaSolicitante.text = resultSet.getString("FechaDeNacimiento")
                        textViewGeneroSolicitante.text = resultSet.getString("Genero")
                        textViewArea.text = resultSet.getString("NombreAreaDeTrabajo")
                        textViewHabilidades.text = resultSet.getString("Habilidades")
                        val fotoUrl = resultSet.getString("Foto")
                        Glide.with(this@Info_Perfil_Solicitante).load(fotoUrl)
                            .into(imgFotoSolicitante)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Info_Perfil_Solicitante,
                            "No se encontraron datos para este solicitante",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Info_Perfil_Solicitante,
                        "Error al consultar la base de datos: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Info_Perfil_Solicitante", "Error en la consulta", e)
                }
            }
        }
    }
}

