package com.example.expogbss

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject




class MapaRutaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val apiKey = "AIzaSyCYV71CqSm2OgpLUVKyVs5xksWz-_AUzFQ" // Reemplaza con tu API Key
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mapa_ruta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnSalirMapa = findViewById<ImageButton>(R.id.btnSalirMapa)
        btnSalirMapa.setOnClickListener {
            finish()  // Cierra la Activity y regresa al flujo anterior
        }

        // Obtener las coordenadas desde el intent
        val latSolicitante = intent.getDoubleExtra("latSolicitante", 0.0)
        val lonSolicitante = intent.getDoubleExtra("lonSolicitante", 0.0)
        val latTrabajo = intent.getDoubleExtra("latTrabajo", 0.0)
        val lonTrabajo = intent.getDoubleExtra("lonTrabajo", 0.0)

        obtenerRuta(LatLng(latSolicitante, lonSolicitante), LatLng(latTrabajo, lonTrabajo))

    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

    }

    private fun obtenerRuta(origen: LatLng, destino: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origen.latitude},${origen.longitude}&" +
                "destination=${destino.latitude},${destino.longitude}&" +
                "key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonObject = JSONObject(responseBody)
                    val routes = jsonObject.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val polyline = routes.getJSONObject(0)
                            .getJSONObject("overview_polyline")
                            .getString("points")

                        val path = PolyUtil.decode(polyline)
                        runOnUiThread {
                            googleMap.addPolyline(
                                PolylineOptions().addAll(path).color(0xFF6200EE.toInt()).width(8f)
                            )
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 12f))
                        }


                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }
}