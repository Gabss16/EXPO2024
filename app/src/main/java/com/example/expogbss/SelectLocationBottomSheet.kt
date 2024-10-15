package com.example.expogbss

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.IOException
import java.util.Locale

class SelectLocationBottomSheet(private val onLocationSelected: (String, Double, Double) -> Unit
) : BottomSheetDialogFragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private var selectedLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_select_location, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.findViewById<Button>(R.id.btnConfirmarUbicacion).setOnClickListener {
            if (selectedLatLng != null) {
                obtenerDireccion(selectedLatLng!!)
            } else {
                Toast.makeText(requireContext(), "Por favor selecciona una ubicación", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }



    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val ubicacionInicial = LatLng(13.6929, -89.2182) // San Salvador
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionInicial, 12f))

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Ubicación Seleccionada"))
            selectedLatLng = latLng
        }

    }

    private fun obtenerDireccion(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            // Intenta obtener una lista de direcciones
            val direcciones: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            // Verifica si la lista no es nula ni está vacía
            if (!direcciones.isNullOrEmpty()) {
                val direccion = direcciones[0].getAddressLine(0) // Accede a la primera dirección
                onLocationSelected(direccion, latLng.latitude, latLng.longitude) // Usa la dirección seleccionada
                dismiss()  // Cierra el BottomSheet
            } else {
                Toast.makeText(requireContext(), "No se encontró una dirección válida", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al obtener la dirección", Toast.LENGTH_SHORT).show()
        }
    }
}