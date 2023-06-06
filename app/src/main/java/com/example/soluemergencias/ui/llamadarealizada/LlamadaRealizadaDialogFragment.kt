package com.example.soluemergencias.ui.llamadarealizada

import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.soluemergencias.BuildConfig
import com.example.soluemergencias.R
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergencia
import com.example.soluemergencias.databinding.DialogFragmentLlamadaRealizadaBinding
import com.example.soluemergencias.utils.*
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LlamadaRealizadaDialogFragment: DialogFragment() {
    private var binding: DialogFragmentLlamadaRealizadaBinding? = null
    private val viewModel: LlamadaRealizadaViewModel by inject()
    private lateinit var sharedPreferences: SharedPreferences
    // Listens for location broadcasts from LocationService.
    private inner class LocationServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(Constants.EXTRA_LOCATION)
            val geoPoint = GeoPoint(location!!.latitude, location.longitude)
            lifecycleScope.launch(Dispatchers.IO){
                viewModel.registrarLocalizacionEnEmergencia(geoPoint)
            }
            Log.e("locationReceived", gettingLocalCurrentDateAndHour().second)
        }
    }
    private var locationServiceBroadcastReceiver = LocationServiceBroadcastReceiver()
    private var locationService: LocationService? = null
    private var locationServiceBound = false

    // Monitors connection to the while-in-use service.
    private val locationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            locationServiceBound = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFragmentLlamadaRealizadaBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("llamadaRealizada", Context.MODE_PRIVATE)
        bindeandoServiceAlFragmentYRegistrandoBroadcastReceiver()

        binding!!.buttonLlamadaRealizadaCancelar.setOnClickListener {
            sharedPreferences.edit().putBoolean("llamadaRealizada", false).apply()
            dismiss()
        }

        binding!!.buttonLlamadaRealizadaGuardar.setOnClickListener{
            requireActivity().closeKeyboard(it)
            guardarLlamadoDeEmergenciaYSuscribirAlServicioSiSeDebe()
        }

        binding!!.buttonLlamadaRealizadaDetenerTransmision.setOnClickListener{
            locationService?.unsubscribeToLocationUpdates()
            dismiss()
        }

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireActivity())
            .unregisterReceiver(locationServiceBroadcastReceiver)
        if (locationServiceBound) {
            requireActivity().unbindService(locationServiceConnection)
            locationServiceBound = false
        }
    }



    private fun guardarLlamadoDeEmergenciaYSuscribirAlServicioSiSeDebe(){
        val comentarios = binding!!.editTextLlamadaRealizadaObservacion.text.toString()
        if (comentarios.isEmpty()) return showToastInMainThread(requireContext(), R.string.completar_campos)

        lifecycleScope.launch(Dispatchers.IO){
            val (date, hour) = gettingLocalCurrentDateAndHour()
            val currentUser = viewModel.dataSource.obtenerUsuarioDesdeRoom()
            val rut = currentUser.rut
            val perfil = currentUser.perfil
            val rutVinculado = if(perfil=="Dueño de casa") currentUser.rut else sharedPreferences.getString("rutVinculado", "")!!
            val currentLocationDirection = getAddressFromLatLng(BuildConfig.GOOGLE_MAPS_API_KEY, getCurrentLocationAsLatLng(requireActivity()))
            val llamadoDeEmergencia = LlamadoDeEmergencia(rut, date, hour, currentLocationDirection, comentarios, rutVinculado)

            val (taskCompleted, response) = viewModel.registrarLlamadoDeEmergencia(llamadoDeEmergencia)

            showToastInMainThread(requireContext(), response)

            if (taskCompleted){
                sharedPreferences.edit().putBoolean("llamadaRealizada", false).apply()
                if(perfil != "Dueño de casa"){
                    lifecycleScope.launch(Dispatchers.Main){
                        binding!!.buttonLlamadaRealizadaGuardar.visibility= View.GONE
                        binding!!.buttonLlamadaRealizadaCancelar.visibility= View.GONE
                        binding!!.editTextLlamadaRealizadaObservacion.visibility= View.GONE
                        binding!!.buttonLlamadaRealizadaDetenerTransmision.visibility= View.VISIBLE
                    }
                    locationService?.subscribeToLocationUpdates()
                }else{
                    dismiss()
                }
            }
        }
    }

    private fun bindeandoServiceAlFragmentYRegistrandoBroadcastReceiver() {
        val serviceIntent = Intent(requireActivity(), LocationService::class.java)
        requireActivity().bindService(serviceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(
                locationServiceBroadcastReceiver,
                IntentFilter(Constants.ACTION_MAP_LOCATION_BROADCAST)
            )
    }

}