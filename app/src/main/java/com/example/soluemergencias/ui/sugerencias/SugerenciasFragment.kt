package com.example.soluemergencias.ui.sugerencias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.soluemergencias.databinding.FragmentSugerenciasBinding

class SugerenciasFragment: Fragment() {
    private var _binding: FragmentSugerenciasBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSugerenciasBinding.inflate(inflater, container, false)

        /*_binding!!.buttonSugerenciasVolver.setOnClickListener{
            findNavController().popBackStack()
        }*/

        return _binding!!.root
    }
}