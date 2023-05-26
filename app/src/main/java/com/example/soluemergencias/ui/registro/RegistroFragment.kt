package com.example.soluemergencias.ui.registro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.adapters.LlamadosDeEmergenciasAdapter
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergenciaEnRecyclerView
import com.example.soluemergencias.databinding.FragmentRegistroBinding
import com.example.soluemergencias.utils.showToastInMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RegistroFragment: Fragment() {
    private var binding: FragmentRegistroBinding? = null
    private val viewModel: RegistroViewModel by inject()
    private val adapter = LlamadosDeEmergenciasAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegistroBinding.inflate(inflater, container, false)
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = this

        binding!!.recyclerViewRegistroRegistroDeActividad.adapter = adapter

        viewModel.llamadosDeEmergencia.observe(viewLifecycleOwner){
            it.let{
                adapter.submitList(it as MutableList<LlamadoDeEmergenciaEnRecyclerView>)
            }
        }

        cargarRecyclerViewSegunPerfil()

        return binding!!.root
    }

    private fun cargarRecyclerViewSegunPerfil() {
        lifecycleScope.launch(Dispatchers.IO) {
            val perfil = viewModel.dataSource.obtenerUsuarioDesdeRoom().perfil
            val (result,message,_) = when (perfil) {
                "Dueño de casa" -> viewModel.cargandoRegistroDeActividadDuenoDeCasa()
                else -> viewModel.cargandoRegistroDeActividadAsesorDelHogar()
            }
            if(!result) showToastInMainThread(requireContext(), message)
        }
    }
}