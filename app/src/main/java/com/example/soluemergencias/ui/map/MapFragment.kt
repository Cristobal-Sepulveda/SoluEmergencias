package com.example.soluemergencias.ui.map

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.databinding.FragmentMapBinding
import com.example.soluemergencias.utils.Constants.cameraDefaultZoom
import com.example.soluemergencias.utils.Constants.defaultLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MapFragment: Fragment(), OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

    private var binding: FragmentMapBinding? = null
    private val viewModel: MapViewModel by inject()
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private val cloudDB = FirebaseFirestore.getInstance()
    private lateinit var iniciandoSnapshotListener: ListenerRegistration
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rutVinculado: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this
        (childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        sharedPreferences = requireContext().getSharedPreferences("rutVinculado", Context.MODE_PRIVATE)

        return binding!!.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        startingPermissionCheckAndMovingCamera()
        lifecycleScope.launch(Dispatchers.IO){
            rutVinculado = viewModel.dataSource.obtenerRutVinculado().rutVinculado
            iniciarSnapshotDelRegistroLocalizacionEnEmergencia()
        }
    }

    private fun startingPermissionCheckAndMovingCamera() {
        val isPermissionGranted = ContextCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if(isPermissionGranted){
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            ), cameraDefaultZoom.toFloat()
                        )
                    )
                } else {
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                defaultLocation.latitude,
                                defaultLocation.longitude
                            ), cameraDefaultZoom.toFloat()
                        )
                    )
                    Toast.makeText(
                        requireActivity(),
                        "No se pudo obtener la ubicación actual",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }else{
            binding!!.map.isGone = true
            binding!!.imageViewMapaSinPermisos.isGone = false
        }
    }

    private fun iniciarSnapshotDelRegistroLocalizacionEnEmergencia() {
        iniciandoSnapshotListener = cloudDB.collection("registroLocalizacionEnEmergencia")
            .addSnapshotListener { snapshot, FirebaseFirestoreException ->
                if (FirebaseFirestoreException != null) {
                    Log.e("FirestoreException", "Error: ${FirebaseFirestoreException.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    for (documentChange in snapshot.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                Log.e("Firestore", "DocumentChange.Type.ADDED")
                                /*chequearSiMiAsesorEstaEnEmergenciaYDibujarMarker(documentChange)*/
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Log.e("Firestore", "DocumentChange.Type.MODIFIED")
                                chequearSiMiAsesorEstaEnEmergenciaYDibujarMarker(documentChange)
                            }
                            DocumentChange.Type.REMOVED -> {
                                map.clear()
                            }
                        }
                    }
                }
            }
    }

    private fun chequearSiMiAsesorEstaEnEmergenciaYDibujarMarker(documentChange: DocumentChange) {
        Log.e("Firestore", "rutVinculado: $rutVinculado")
        Log.e("Firestore", "documentChange.document.id: ${documentChange.document.id}")
        if (documentChange.document.id == rutVinculado) {
            map.clear()
            val geoPoints = documentChange.document.get("geoPoints") as List<*>
            val geoPointDeInteres = geoPoints.last() as GeoPoint
            val latLng = LatLng(geoPointDeInteres.latitude, geoPointDeInteres.longitude)
            val markerOptions = MarkerOptions().position(latLng)
            map.addMarker(markerOptions)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //esto detiene el snapshot listener del RegistroTrayectoVolanteros de la cloudDB
        iniciandoSnapshotListener.remove()
    }
}