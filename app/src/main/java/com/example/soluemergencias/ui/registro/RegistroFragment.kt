package com.example.soluemergencias.ui.registro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.soluemergencias.databinding.FragmentRegistroBinding

class RegistroFragment: Fragment() {
    private var _binding: FragmentRegistroBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)

        return _binding!!.root
    }
}