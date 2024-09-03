package com.example.expogbss.filtro

import RecicleViewHelpers.AdaptadorTrabajos
import android.widget.Filter
import modelo.Trabajo
import java.util.Locale

class FiltrarAnuncio (
    private val adaptador : AdaptadorTrabajos,
    private val filtroLista : ArrayList<Trabajo>
): Filter(){
    override fun performFiltering(filtro: CharSequence?): FilterResults {

        var filtro = filtro
        val resultados = FilterResults()

        if (!filtro.isNullOrEmpty()){
            filtro = filtro.toString().uppercase(Locale.getDefault())
            val filtroModelo = ArrayList<Trabajo>()
            for (i in filtroLista.indices){
                if (filtroLista[i].Titulo.uppercase(Locale.getDefault()).contains(filtro) ){
                    filtroModelo.add(filtroLista[i])
                }
            }
            resultados.count = filtroModelo.size
            resultados.values = filtroModelo
        }else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados

    }

    override fun publishResults(filtro: CharSequence?, resultados: FilterResults?) {

    }
}