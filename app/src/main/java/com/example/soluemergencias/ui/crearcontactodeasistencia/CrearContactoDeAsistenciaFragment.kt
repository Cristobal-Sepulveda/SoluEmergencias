package com.example.soluemergencias.ui.crearcontactodeasistencia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.databinding.FragmentCrearContactoDeAsistenciaBinding
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class CrearContactoDeAsistenciaFragment: BottomSheetDialogFragment() {
    private var _binding: FragmentCrearContactoDeAsistenciaBinding? = null
    private val _viewModel: CrearContactoDeAsistenciaViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentCrearContactoDeAsistenciaBinding.inflate(inflater, container, false)

        _binding!!.buttonCrearContactoDeAsistenciaVolver.setOnClickListener { dismiss() }
        _binding!!.buttonCrearContactoDeAsistenciaConfirmar.setOnClickListener{ crearContactoDeAsistencia() }

        return _binding!!.root
    }
    private fun crearContactoDeAsistencia() {
        val nombre = _binding!!.editTextCrearContactoDeAsistenciaNombre.text.toString()
        val telefono = _binding!!.editTextCrearContactoDeAsistenciaTelefono.text.toString()
        if (nombre.isEmpty() || telefono.isEmpty()) {
            showToastInMainThreadWithStringResource(requireContext(), R.string.formulario_incompleto)
            return
        }
        lifecycleScope.launch(Dispatchers.IO){
            val task = _viewModel.crearContactoDeAsistencia(nombre, telefono)
            showToastInMainThreadWithStringResource(requireContext(), task.second)
        }
    }


}