package com.example.soluemergencias.ui.vistageneral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.adapters.ContactoDeEmergenciaAdapter
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.databinding.FragmentVistaGeneralBinding
import com.example.soluemergencias.ui.crearcontactodeasistencia.CrearContactoDeAsistenciaFragment
import com.example.soluemergencias.utils.Constants.defaultContactosDeEmergencia
import com.example.soluemergencias.utils.parsingBase64ImageToBitMap
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class VistaGeneralFragment : Fragment() {
    private var _binding: FragmentVistaGeneralBinding? = null
    private val _viewModel: VistaGeneralViewModel by inject()
    private val _appDataSource: AppDataSource by inject()
    private val adapter = ContactoDeEmergenciaAdapter(
        _viewModel, _appDataSource, ContactoDeEmergenciaAdapter.OnClickListener { }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVistaGeneralBinding.inflate(inflater, container, false)
        _binding!!.viewModel = _viewModel
        _binding!!.lifecycleOwner = this
        _binding!!.recyclerviewVistaGeneralListadoDeEmergencias.adapter = adapter

        /*Click Listeners*/
        _binding!!.fabVistaGeneralCrearContactoDeAsistencia.setOnClickListener {
            val dialogFragment = CrearContactoDeAsistenciaFragment()
            dialogFragment.show(
                requireActivity().supportFragmentManager,
                "CrearContactoDeAsistencia"
            )
        }
        _binding!!.includeVistaGeneralBanner
            .buttonVistageneralBannerDesvincular.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    val task = _viewModel.desvincularUsuarios()
                    showToastInMainThreadWithStringResource(requireContext(), task.second)
                    lifecycleScope.launch(Dispatchers.Main) {
                        requireActivity().recreate()
                    }
                }
            }

        /*Observers*/
        _viewModel.contactosDeEmergenciaInScreen.observe(viewLifecycleOwner) {
            it.let {
                adapter.submitList(it as MutableList<ContactoDeEmergencia>)
            }
        }

        /*Desplegando contenido según el caso*/
        validarSiDeboMostrarElFabButton()
        cargandoListaDeContactosDeEmergencia()
        completarContenidoDelBanner()

        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val aux = defaultContactosDeEmergencia.filter {
            it.id.length != 2
        }
        defaultContactosDeEmergencia = aux as MutableList<ContactoDeEmergencia>
    }

    private fun validarSiDeboMostrarElFabButton() {
        val fabCrearContactoDeEmergencia = _binding!!.fabVistaGeneralCrearContactoDeAsistencia

        lifecycleScope.launch(Dispatchers.IO) {
            val perfil = _appDataSource.obtenerUsuarioDesdeRoom().perfil
            lifecycleScope.launch(Dispatchers.Main) {
                if(perfil!="Dueño de casa"){
                    fabCrearContactoDeEmergencia.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun cargandoListaDeContactosDeEmergencia() {
        lifecycleScope.launch(Dispatchers.IO) {
            val task = _viewModel.cargandoListaDeContactosDeEmergencia()
            if (!task.first) showToastInMainThreadWithStringResource(requireContext(), task.second)

        }
    }

    private fun completarContenidoDelBanner() {
        val botonDesvincular = _binding!!.includeVistaGeneralBanner
            .buttonVistageneralBannerDesvincular
        val imgFotoPerfil = _binding!!.includeVistaGeneralBanner
            .imageViewVistaGeneralBannerFotoPerfil
        val textoNombreCompleto = _binding!!.includeVistaGeneralBanner
            .textViewVistageneralBannerNombreYApellido
        val textoRutVinculado = _binding!!.includeVistaGeneralBanner
            .textViewVistageneralBannerRut

        lifecycleScope.launch(Dispatchers.IO) {
            val userLocalData = _appDataSource.obtenerUsuarioDesdeRoom()
            val task = _viewModel.obtenerUsuarioVinculado()

            lifecycleScope.launch(Dispatchers.Main) {
                val nombreCompletoDelUsuario = userLocalData.nombreCompleto.split(" ")
                val nombreYApellido = "${nombreCompletoDelUsuario[0]}\n${nombreCompletoDelUsuario[1]}"

                textoNombreCompleto.text = nombreYApellido
                imgFotoPerfil.setImageBitmap(parsingBase64ImageToBitMap(userLocalData.fotoPerfil))

                if (task.third == null) {
                    textoRutVinculado.setText(R.string.contacte_administrador)
                    botonDesvincular.apply {
                        isEnabled = false
                        setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.app_logo,
                                null
                            )
                        )
                    }
                } else {
                    if (task.third!!.rutVinculado == "") {
                        textoRutVinculado.text = "Cuenta no vinculada."
                        botonDesvincular.apply {
                            isEnabled = false
                            setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.app_logo,
                                    null
                                )
                            )
                        }
                    } else {
                        textoRutVinculado.text = "Vinculado al rut: \n${task.third!!.rutVinculado}"
                        botonDesvincular.apply {
                            setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.boton_desvincular,
                                    null
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}