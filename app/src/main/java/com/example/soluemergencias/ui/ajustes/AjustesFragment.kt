package com.example.soluemergencias.ui.ajustes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import com.example.soluemergencias.databinding.FragmentAjustesBinding

class AjustesFragment: Fragment() {
    private var _binding : FragmentAjustesBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)

        _binding!!.switchAjustesDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                recreateFragment()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                recreateFragment()
            }
        }
        return _binding!!.root
    }
    private fun recreateFragment() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.detach(this)
        fragmentTransaction.attach(this)
        fragmentTransaction.commit()
    }
}