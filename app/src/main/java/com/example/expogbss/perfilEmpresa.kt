package com.example.expogbss

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class perfilEmpresa : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_perfil_empresa, container, false)

        val textViewNombreEmpresa = root.findViewById<TextView>(R.id.textViewNombreEmpresa)
        val textViewCorreoEmpresa = root.findViewById<TextView>(R.id.textViewCorreoEmpresa)
        val textViewNombreEmpleador = root.findViewById<TextView>(R.id.textViewNombreEmpleador)
        val textViewNumeroEmpleador = root.findViewById<TextView>(R.id.textViewNumeroEmpleador)
        val textViewDireccionEmpleador = root.findViewById<TextView>(R.id.textViewDireccionEmpleador)
        val textViewSitioWeb = root.findViewById<TextView>(R.id.textViewSitioWeb)
        val imgFotoEmpleador = root.findViewById<ImageView>(R.id.imgFotoEmpleador)

        textViewNombreEmpresa.text = login.nombreEmpresa
        textViewCorreoEmpresa.text = login.correoEmpleador
        textViewNombreEmpleador.text = login.nombreEmpleador
        textViewNumeroEmpleador.text = login.numeroEmpleador
        textViewDireccionEmpleador.text = login.direccionEmpleador
        textViewSitioWeb.text = login.sitioWebEmpleador
        //imgFotoEmpleador.setImageDrawable(BitmapDrawable.createFromPath(login.fotoEmpleador))
        Glide.with(this).load(login.fotoEmpleador).into(imgFotoEmpleador)
        return root

    }


}