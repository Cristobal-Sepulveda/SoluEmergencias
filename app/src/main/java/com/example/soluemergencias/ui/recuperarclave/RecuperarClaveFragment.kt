package com.example.soluemergencias.ui.recuperarclave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.databinding.FragmentRecuperarClaveBinding
import com.example.soluemergencias.utils.closeKeyboard
import com.example.soluemergencias.utils.showToastInMainThreadWithHardcoreString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RecuperarClaveFragment: DialogFragment() {
    private var _binding: FragmentRecuperarClaveBinding? = null
    private val _viewModel: RecuperarClaveViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        _binding = FragmentRecuperarClaveBinding.inflate(inflater, container, false)
        _binding!!.buttonRecuperarClaveVolver.setOnClickListener{ dismiss() }
        _binding!!.buttonRecuperarClaveConfirmar.setOnClickListener{
            requireActivity().closeKeyboard(it)
            recuperarClave()
        }
        return _binding!!.root
    }

    private fun recuperarClave(){
        lifecycleScope.launch(Dispatchers.IO){
            val email = _binding!!.editTextRecuperarClaveIngreseRut.text.toString()
            showToastInMainThreadWithHardcoreString(requireContext(), _viewModel.recuperarClave(email).second)

        }
    }
}