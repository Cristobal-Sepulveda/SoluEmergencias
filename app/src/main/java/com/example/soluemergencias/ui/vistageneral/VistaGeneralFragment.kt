package com.example.soluemergencias.ui.vistageneral

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.adapters.ContactoDeEmergenciaAdapter
import com.example.soluemergencias.data.data_objects.dbo.RutVinculadoDBO
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.databinding.FragmentVistaGeneralBinding
import com.example.soluemergencias.ui.crearcontactodeasistencia.CrearContactoDeAsistenciaFragment
import com.example.soluemergencias.ui.llamadarealizada.LlamadaRealizadaDialogFragment
import com.example.soluemergencias.utils.Constants.defaultContactosDeEmergencia
import com.example.soluemergencias.utils.parsingBase64ImageToBitMap
import com.example.soluemergencias.utils.showToastInMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class VistaGeneralFragment : Fragment() {
    private var _binding: FragmentVistaGeneralBinding? = null
    private val _viewModel: VistaGeneralViewModel by inject()
    private lateinit var userLocalData : UsuarioDBO
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: ContactoDeEmergenciaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVistaGeneralBinding.inflate(inflater, container, false)
        _binding!!.viewModel = _viewModel
        _binding!!.lifecycleOwner = this
        sharedPreferences = requireContext().getSharedPreferences("llamadaRealizada", Context.MODE_PRIVATE)
        adapter = ContactoDeEmergenciaAdapter(sharedPreferences)
        _binding!!.recyclerviewVistaGeneralListadoDeEmergencias.adapter = adapter

        /*Click Listeners*/
        _binding!!.fabVistaGeneralCrearContactoDeAsistencia.setOnClickListener {
            val dialogFragment = CrearContactoDeAsistenciaFragment()
            dialogFragment.show(requireActivity().supportFragmentManager, "")
        }

        _binding!!.includeVistaGeneralBanner.buttonVistageneralBannerDesvincular.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val task = _viewModel.desvincularUsuarios()
                showToastInMainThread(requireContext(), task.second)
                lifecycleScope.launch(Dispatchers.Main) { requireActivity().recreate() }
            }
        }

        /*Observers*/
        _viewModel.contactosDeEmergenciaInScreen.observe(viewLifecycleOwner) {
            it.let { adapter.submitList(it as MutableList<ContactoDeEmergencia>) }
        }

        validarSiDeboMostrarElFabButton()
        cargandoListaDeContactosDeEmergencia()
        completarContenidoDelBanner()

        return _binding!!.root
    }

    override fun onStart(){
        super.onStart()
        val aux = sharedPreferences.getBoolean("llamadaRealizada", false)
        if(aux){
            LlamadaRealizadaDialogFragment()
                .show(requireActivity().supportFragmentManager, "LlamadaRealizada")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val aux = defaultContactosDeEmergencia.filter { it.id.length != 2 }
        defaultContactosDeEmergencia = aux as MutableList<ContactoDeEmergencia>
    }

    private fun validarSiDeboMostrarElFabButton() {
        lifecycleScope.launch(Dispatchers.IO){
            userLocalData = _viewModel.obtenerUsuarioDesdeRoom()
            if(userLocalData.perfil != "Dueño de casa"){
                _binding!!.fabVistaGeneralCrearContactoDeAsistencia.visibility = View.INVISIBLE
            }
        }
    }

    private fun cargandoListaDeContactosDeEmergencia() {
        lifecycleScope.launch(Dispatchers.IO) {
            val task = _viewModel.cargandoListaDeContactosDeEmergencia()
            if (!task.first) showToastInMainThread(requireContext(), task.second)

        }
    }

    private fun completarContenidoDelBanner() {
        lifecycleScope.launch(Dispatchers.IO) {
            val task = _viewModel.obtenerUsuarioVinculado()

            lifecycleScope.launch(Dispatchers.Main) {
                val botonDesvincular = _binding!!.includeVistaGeneralBanner
                    .buttonVistageneralBannerDesvincular
                val imgFotoPerfil = _binding!!.includeVistaGeneralBanner
                    .imageViewVistaGeneralBannerFotoPerfil
                val textoNombreCompleto = _binding!!.includeVistaGeneralBanner
                    .textViewVistageneralBannerNombreYApellido
                val textoRutVinculado = _binding!!.includeVistaGeneralBanner
                    .textViewVistageneralBannerRut

                val nombreCompletoDelUsuario = userLocalData.nombreCompleto.split(" ")
                val nombreYApellido = "${nombreCompletoDelUsuario[0]}\n${nombreCompletoDelUsuario[1]}"
                textoNombreCompleto.text = nombreYApellido
                imgFotoPerfil.setImageBitmap(parsingBase64ImageToBitMap(userLocalData.fotoPerfil))

                if (task.third == null) {
                    sharedPreferences.edit().putString("rutVinculado", "").apply()
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
                        sharedPreferences.edit().putString("rutVinculado", "").apply()
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
                        sharedPreferences.edit().putString("rutVinculado", task.third!!.rutVinculado).apply()
                        _viewModel.dataSource.guardarRutVinculado(RutVinculadoDBO(task.third!!.rutVinculado))
                        textoRutVinculado.text = "Vinculado al rut: \n${task.third!!.rutVinculado}"
                        val rutVinculado = sharedPreferences.getString("rutVinculado", "")
                        Log.e("Firestore", "rutVinculado: $rutVinculado")
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