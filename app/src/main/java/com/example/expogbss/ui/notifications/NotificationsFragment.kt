package com.example.expogbss.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.expogbss.R
import com.example.expogbss.databinding.FragmentNotificationsBinding
import com.example.expogbss.editar_perfil_Empleador
import com.example.expogbss.login
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

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

        // Manejar el evento de clic en el botón de editar perfil
        btnEditarSolicitante.setOnClickListener {
            // Iniciar la actividad "editar_perfil_Empleador"
            val intent = Intent(activity, editar_perfil_Empleador::class.java)
            startActivity(intent)
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