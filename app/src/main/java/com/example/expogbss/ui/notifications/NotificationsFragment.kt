package com.example.expogbss.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.expogbss.R
import com.example.expogbss.databinding.FragmentNotificationsBinding
import com.example.expogbss.login

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

        textViewNombreSolicitante.text = login.nombresSolicitante
        textViewCorreoSolicitante.text = login.correoSolicitante
        textViewNumeroSolicitante.text = login.numeroSolicitante
        textViewDireccionSolicitante.text = login.direccionEmpleador
        textViewDepartamento.text = login.departamentoSolicitante
        textViewFechaSolicitante.text = login.fechaNacimiento
        textViewGeneroSolicitante.text = login.generoSolicitante
        textViewArea.text = login.areaDeTrabajo
        textViewHabilidades.text = login.areaDeTrabajo
        Glide.with(this).load(login.fotoSolicitante).into(imgFotoSolicitante)





        return root
    }


}