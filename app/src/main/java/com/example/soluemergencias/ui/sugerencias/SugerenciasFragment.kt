package com.example.soluemergencias.ui.sugerencias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.soluemergencias.databinding.FragmentSugerenciasBinding
import com.example.soluemergencias.utils.closeKeyboard
import com.example.soluemergencias.utils.showToastInMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SugerenciasFragment: Fragment() {
    private var _binding: FragmentSugerenciasBinding? = null
    private val _viewModel: SugerenciasViewModel by inject()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSugerenciasBinding.inflate(inflater, container, false)

        _binding!!.buttonSugerenciasVolver.setOnClickListener{
            findNavController().popBackStack()
        }
        _binding!!.buttonSugerenciasConfirmar.setOnClickListener{
            requireActivity().closeKeyboard(it)
            enviarSugerencia()
        }
        return _binding!!.root
    }

    private fun enviarSugerencia(){

        lifecycleScope.launch(Dispatchers.IO){
            val task = _viewModel.enviarSugerencia(_binding!!.editTextSugerenciasComentarios.text.toString())
             showToastInMainThread(requireContext(), task.second)
        }
    }
}