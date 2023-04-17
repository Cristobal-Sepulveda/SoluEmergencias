package com.example.soluemergencias.ui.vistageneral

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.soluemergencias.R
import com.example.soluemergencias.databinding.FragmentVistaGeneralBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class VistaGeneralFragment : Fragment() {

    private var _binding: FragmentVistaGeneralBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentVistaGeneralBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_vistageneralfragment_to_navigation_perfil)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}