package com.example.soluemergencias.ui.vincularcuentas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.adapters.SolicitudEnviadaAdapter
import com.example.soluemergencias.adapters.SolicitudRecibidaAdapter
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.databinding.FragmentVincularCuentasBinding
import com.example.soluemergencias.utils.closeKeyboard
import com.example.soluemergencias.utils.isThisRutValid
import com.example.soluemergencias.utils.showAlertWithStringResources
import com.example.soluemergencias.utils.showToastInMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class VincularCuentasFragment: Fragment() {
    private var _binding: FragmentVincularCuentasBinding? = null
    private val _viewModel: VincularCuentasViewModel by inject()
    private val _appDataSource: AppDataSource by inject()
    private val adapterAprobar = SolicitudRecibidaAdapter(_viewModel,_appDataSource , SolicitudRecibidaAdapter.OnClickListener{ })
    private val adapterEnviadas = SolicitudEnviadaAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentVincularCuentasBinding.inflate(inflater, container, false)
        _binding!!.lifecycleOwner = this
        _binding!!.viewModel = _viewModel
        _binding!!.recyclerviewVincularCuentasSolicitudesPorAprobar.adapter = adapterAprobar
        _binding!!.recyclerviewVincularCuentasSolicitudesEnviadas.adapter = adapterEnviadas

        //Click listeners
        _binding!!.buttonVincularCuentasSolicitar.setOnClickListener{
            enviarSolicitudDeVinculacion(_binding!!.editTextVincularCuentasRut.text.toString(), it)
        }

        //Observers
        _viewModel.solicitudesRecibidasInScreen.observe(requireActivity()){
            it.let { adapterAprobar.submitList(it as MutableList<SolicitudDeVinculo>) }
        }

        _viewModel.solicitudesEnviadasInScreen.observe(requireActivity()){
            it.let { adapterEnviadas.submitList(it as MutableList<SolicitudDeVinculo>) }
        }

        chequearSiHaySolicitudesDeVinculacionRecibidasSinGestionar()
        chequearSiHaySolicitudesDeVinculacionEnviadas()

        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewModel.vaciarRecyclerView()
    }

    private fun chequearSiHaySolicitudesDeVinculacionRecibidasSinGestionar() {
        lifecycleScope.launch(Dispatchers.IO){
            val request = _viewModel.chequearSiHaySolicitudesDeVinculacionRecibidasSinGestionar()
            if(!request.first) showToastInMainThread(requireContext(), request.second)
        }
    }

    private fun chequearSiHaySolicitudesDeVinculacionEnviadas() {
        lifecycleScope.launch(Dispatchers.IO){
            val request = _viewModel.chequearSiHaySolicitudesDeVinculacionEnviadas()
            if(!request.first) showToastInMainThread(requireContext(), request.second)
        }
    }

    private fun enviarSolicitudDeVinculacion(rut: String, it: View) {
        if(!isThisRutValid(rut)){
            showToastInMainThread(requireContext(), R.string.rut_invalido)
            return
        }
        requireActivity().apply{
            closeKeyboard(it)
            showAlertWithStringResources(R.string.atencion, R.string.vincular_cuentas_solicitar_confirmacion
            ){
                lifecycleScope.launch(Dispatchers.IO){
                    val request = _viewModel.crearSolicitudDeVinculo(rut)
                    showToastInMainThread(requireContext(),request.second)
                }
            }
        }
    }

}