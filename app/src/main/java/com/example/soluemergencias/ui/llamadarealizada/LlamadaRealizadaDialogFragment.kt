package com.example.soluemergencias.ui.llamadarealizada

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.databinding.DialogFragmentLlamadaRealizadaBinding
import com.example.soluemergencias.utils.closeKeyboard
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LlamadaRealizadaDialogFragment: DialogFragment() {
    private var binding: DialogFragmentLlamadaRealizadaBinding? = null
    private val viewModel: LlamadaRealizadaViewModel by inject()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentLlamadaRealizadaBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences(
            "llamadaRealizada", Context.MODE_PRIVATE
        )

        binding!!.buttonLlamadaRealizadaCancelar.setOnClickListener {
            requireActivity().closeKeyboard(it)
            lifecycleScope.launch(Dispatchers.IO){
                desestimarLlamada()
            }
        }

        binding!!.buttonLlamadaRealizadaGuardar.setOnClickListener{
            val comentarios = binding!!.editTextLlamadaRealizadaObservacion.text.toString()
            if(comentarios.isEmpty()){
                showToastInMainThreadWithStringResource(
                    requireContext(),
                    R.string.completar_campos
                )
                return@setOnClickListener
            }
            lifecycleScope.launch(Dispatchers.IO){
                val task = viewModel.guardarComentarioDeLaEmergencia(comentarios)
                requireActivity().closeKeyboard(it)
                showToastInMainThreadWithStringResource(
                    requireContext(),
                    task.second
                )
                if(task.first){
                    sharedPreferences.edit().putBoolean("llamadaRealizada", false).apply()
                }
                dismiss()
            }
        }

        return binding!!.root
    }


    private suspend fun desestimarLlamada(){
        val task = viewModel.ignorarEmergencia()
        if(task.first) {
            sharedPreferences.edit().putBoolean("llamadaRealizada", false).apply()
        }
        showToastInMainThreadWithStringResource(
            requireContext(),
            task.second
        )
        dismiss()
    }
}