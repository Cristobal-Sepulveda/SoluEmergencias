package com.example.soluemergencias.ui.vincularcuentas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.adapters.SolicitudDeVinculoAdapter
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.databinding.FragmentVincularCuentasBinding
import com.example.soluemergencias.utils.closeKeyboard
import com.example.soluemergencias.utils.isThisRutValid
import com.example.soluemergencias.utils.showAlertWithStringResources
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class VincularCuentasFragment: Fragment() {
    private var _binding: FragmentVincularCuentasBinding? = null
    private val _viewModel: VincularCuentasViewModel by inject()
    private val _appDataSource: AppDataSource by inject()
    private val adapter = SolicitudDeVinculoAdapter(_viewModel,_appDataSource , SolicitudDeVinculoAdapter.OnClickListener{ })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentVincularCuentasBinding.inflate(inflater, container, false)
        _binding!!.lifecycleOwner = this
        _binding!!.viewModel = _viewModel
        _binding!!.recyclerviewVincularCuentasListadoDeAsesoras.adapter = adapter
        chequearSiHaySolicitudesPorAprobar()

        //Click listeners
        _binding!!.buttonVincularCuentasSolicitar.setOnClickListener{
            enviarSolicitudDeVinculacion(
                _binding!!.textInputEditTextVincularCuentasRut.text.toString(), it
            )
        }

        //Observers
        _viewModel.solicitudesInScreen.observe(requireActivity()){
            it.let { adapter.submitList(it as MutableList<SolicitudDeVinculo>) }
        }

        return _binding!!.root
    }

    private fun chequearSiHaySolicitudesPorAprobar() {
        lifecycleScope.launch(Dispatchers.IO){
            val request = _viewModel.chequearSiHaySolicitudesPorAprobar()
            if(!request.first) showToastInMainThreadWithStringResource(requireContext(), request.second)
        }
    }

    private fun enviarSolicitudDeVinculacion(rut: String, it: View) {
        if(!isThisRutValid(rut)){
            showToastInMainThreadWithStringResource(requireContext(), R.string.rut_invalido)
            return
        }
        requireActivity().apply{
            closeKeyboard(it)
            showAlertWithStringResources(R.string.atencion, R.string.vincular_cuentas_solicitar_confirmacion
            ){
                lifecycleScope.launch(Dispatchers.IO){
                    val request = _viewModel.crearSolicitudDeVinculo(rut)
                    showToastInMainThreadWithStringResource(requireContext(),request.second)
                }
            }
        }
    }

}