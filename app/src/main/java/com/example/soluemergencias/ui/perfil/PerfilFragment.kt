package com.example.soluemergencias.ui.perfil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.soluemergencias.R
import com.example.soluemergencias.databinding.FragmentPerfilBinding
import org.koin.android.ext.android.inject

class PerfilFragment() : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val _viewModel: PerfilViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        _binding!!.apply{
            this.buttonSecond.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_perfil_to_navigation_vistageneralfragment)
            }
        }
        return _binding!!.root
    }
}