package com.example.soluemergencias.ui.vistageneral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.soluemergencias.R
import com.example.soluemergencias.adapters.ContactoDeEmergenciaAdapter
import com.example.soluemergencias.adapters.SolicitudDeVinculoAdapter
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.databinding.FragmentVistaGeneralBinding
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class VistaGeneralFragment : Fragment() {
    private var _binding: FragmentVistaGeneralBinding? = null
    private val _viewModel: VistaGeneralViewModel by inject()
    private val _appDataSource: AppDataSource by inject()
    private val adapter = ContactoDeEmergenciaAdapter(_viewModel,_appDataSource , ContactoDeEmergenciaAdapter.OnClickListener{ })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVistaGeneralBinding.inflate(inflater, container, false)
        _binding!!.viewModel = _viewModel
        _binding!!.lifecycleOwner = this
        _binding!!.recyclerviewVistaGeneralListadoDeEmergencias.adapter = adapter

        _viewModel.contactosDeEmergenciaInScreen.observe(viewLifecycleOwner){
            it.let { adapter.submitList(it as MutableList<ContactoDeEmergencia>) }
        }

        cargandoListaDeContactosDeEmergencia()

        return _binding!!.root
    }
    private fun cargandoListaDeContactosDeEmergencia() {
        lifecycleScope.launch(Dispatchers.IO){
            val task = _viewModel.cargandoListaDeContactosDeEmergencia()
            if(!task.first) showToastInMainThreadWithStringResource(requireContext(), task.second)

        }
    }
}