package com.example.expogbss.ui.notifications

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.expogbss.Cambiar_ContrasenaEmpleador
import com.example.expogbss.R
import com.example.expogbss.cambiar_contrasena_Solicitante
import com.example.expogbss.databinding.FragmentNotificationsBinding
import com.example.expogbss.editar_perfil_Empleador
import com.example.expogbss.editar_perfil_solicitante
import com.example.expogbss.login
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena.IdSolicitante
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Solicitante

class NotificationsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        val textViewNombreSolicitante = root.findViewById<TextView>(R.id.textViewNombreSolicitante)
        val textViewCorreoSolicitante = root.findViewById<TextView>(R.id.textViewCorreoSolicitante)
        val textViewNumeroSolicitante = root.findViewById<TextView>(R.id.textViewNumeroSolicitante)
        val textViewDireccionSolicitante = root.findViewById<TextView>(R.id.textViewDireccionSolicitante)
        val textViewDepartamento = root.findViewById<TextView>(R.id.textViewDepartamento)
        val textViewFechaSolicitante = root.findViewById<TextView>(R.id.textViewFechaSolicitante)
        val textViewGeneroSolicitante = root.findViewById<TextView>(R.id.textViewGeneroSolicitante)
        val textViewArea = root.findViewById<TextView>(R.id.textViewArea)
        val textViewHabilidades = root.findViewById<TextView>(R.id.textViewHabilidades)
        val imgFotoSolicitante = root.findViewById<ImageView>(R.id.imgFotoSolicitante)
        val btnEditarSolicitante = root.findViewById<ImageView>(R.id.btnEditarSolicitante)
        val btnDescargarCV = root.findViewById<ImageView>(R.id.btnDescargarCV)

        // Manejar el evento de clic en el botón de editar perfil
        btnEditarSolicitante.setOnClickListener {
            // Iniciar la actividad "editar_perfil_Empleador"
            val intent = Intent(activity, editar_perfil_solicitante::class.java)
            startActivity(intent)
        }
        val btnCerrarSesion = root.findViewById<TextView>(R.id.btnCerrarSesionSolicitante)

        // Manejar el evento de clic en el botón de Cerrar sesión
        btnCerrarSesion.setOnClickListener {
            // Iniciar la actividad "editar_perfil_Empleador"
            val login = Intent(activity, login::class.java)
            login.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(login)
        }

       fun downloadPDF(urlCV: String, nombreSolicitante: String) {
            try {
                val request = DownloadManager.Request(Uri.parse(urlCV))
                request.setTitle("Curriculum Vitae de $nombreSolicitante")
                request.setDescription("Descargando archivo PDF del curriculum")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Curriculum-$nombreSolicitante.pdf")

                val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)

                Toast.makeText(requireContext(), "Descarga iniciada...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al descargar el archivo", Toast.LENGTH_SHORT).show()
                Log.e("DownloadError", e.message.toString())
            }
        }

        val btnEditarContrasena = root.findViewById<ImageView>(R.id.btnEditarContrasenaSolicitante)

        // Manejar el evento de clic en el botón de editar contraseña
        btnEditarContrasena.setOnClickListener {
            // Iniciar la actividad "Cambiar_ContrasenaEmpleador"
            val intent = Intent(activity, cambiar_contrasena_Solicitante::class.java)
            startActivity(intent)
        }

        // Manejar el evento de clic en el botón de descargar CV
        btnDescargarCV.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val objConexion = ClaseConexion().cadenaConexion()
                    val query = """
                SELECT Curriculum, Nombre 
                FROM SOLICITANTE 
                WHERE CorreoElectronico = ?
            """
                    val statement = objConexion?.prepareStatement(query)
                    statement?.setString(1, login.correoLogin)
                    val resultSet = statement?.executeQuery()

                    if (resultSet?.next() == true) {
                        val urlCV = resultSet.getString(1) // Obtén la URL del CV desde la base de datos
                        val nombreSolicitante = resultSet.getString(2) // Obtén el nombre del solicitante

                        withContext(Dispatchers.Main) {
                            if (urlCV != null && urlCV.isNotEmpty()) {
                                downloadPDF(urlCV, nombreSolicitante) // Llamar a la función para descargar el PDF con el nombre
                            } else {
                                Toast.makeText(requireContext(), "No se encontró el CV", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al descargar el CV", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }



        // Realiza la consulta en un hilo secundario usando corrutinas
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().cadenaConexion()

                val query = """
                    SELECT 
                        s.Nombre, 
                        s.CorreoElectronico, 
                        s.Telefono, 
                        s.Direccion, 
                        d.Nombre, 
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
                        s.CorreoElectronico = ?
                """

                val statement = objConexion?.prepareStatement(query)
                statement?.setString(1, login.correoLogin)
                val resultSet = statement?.executeQuery()

                if (resultSet?.next() == true) {
                    // Actualiza los TextView en el hilo principal
                    withContext(Dispatchers.Main) {
                        textViewNombreSolicitante.text = resultSet.getString(1)
                        textViewCorreoSolicitante.text = resultSet.getString(2)
                        textViewNumeroSolicitante.text = resultSet.getString(3)
                        textViewDireccionSolicitante.text = resultSet.getString(4)
                        textViewDepartamento.text = resultSet.getString(5)
                        textViewFechaSolicitante.text = resultSet.getString(6)
                        textViewGeneroSolicitante.text = resultSet.getString(7)
                        textViewArea.text = resultSet.getString(8)
                        textViewHabilidades.text = resultSet.getString(9)
                        val fotoUrl = resultSet.getString(10)
                        Glide.with(this@NotificationsFragment).load(fotoUrl).into(imgFotoSolicitante)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al consultar la base de datos", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return root
    }


}