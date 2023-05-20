package com.example.soluemergencias.ui.vistageneral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.adapters.ContactoDeEmergenciaAdapter
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.databinding.FragmentVistaGeneralBinding
import com.example.soluemergencias.ui.crearcontactodeasistencia.CrearContactoDeAsistenciaFragment
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

        _binding!!.fabVistaGeneralCrearContactoDeAsistencia.setOnClickListener{
            val dialogFragment = CrearContactoDeAsistenciaFragment()
            dialogFragment.show(requireActivity().supportFragmentManager, "CrearContactoDeAsistencia")
        }

        _viewModel.contactosDeEmergenciaInScreen.observe(viewLifecycleOwner){
            it.let {
                adapter.submitList(it as MutableList<ContactoDeEmergencia>)
                it.forEach{ contactoDeEmergencia ->
                    if(!contactoDeEmergencia.rut.isNullOrBlank()){
                        val aux = "Tu cuenta está vinculada al rut: ${contactoDeEmergencia.rut}"
                        _binding!!.textViewVistaGeneralRutVinculado.text = aux
                    }
                }
            }
        }

        editarUISegunPerfil()
        cargandoListaDeContactosDeEmergencia()

        return _binding!!.root
    }

    private fun editarUISegunPerfil() {
        lifecycleScope.launch(Dispatchers.IO) {
            val user = _appDataSource.obtenerUsuarioDesdeRoom()
            if (user.perfil != "Dueño de casa") {
                lifecycleScope.launch(Dispatchers.Main) {
                    _binding!!.fabVistaGeneralCrearContactoDeAsistencia.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun cargandoListaDeContactosDeEmergencia() {
        lifecycleScope.launch(Dispatchers.IO){
            val task = _viewModel.cargandoListaDeContactosDeEmergencia()
            if(!task.first) showToastInMainThreadWithStringResource(requireContext(), task.second)

        }
    }
}