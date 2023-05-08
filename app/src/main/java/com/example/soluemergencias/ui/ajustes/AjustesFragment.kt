package com.example.soluemergencias.ui.ajustes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.soluemergencias.databinding.FragmentAjustesBinding

class AjustesFragment: Fragment() {
    private var _binding : FragmentAjustesBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return _binding!!.root
    }
}