package com.example.soluemergencias.ui.acercade

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.soluemergencias.databinding.FragmentAcercaDeBinding

class AcercaDeFragment: Fragment() {
    private var _binding: FragmentAcercaDeBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAcercaDeBinding.inflate(inflater, container, false)

        _binding!!.buttonAcercaDeVolver.setOnClickListener{
            findNavController().popBackStack()
        }
        return _binding!!.root
    }
}