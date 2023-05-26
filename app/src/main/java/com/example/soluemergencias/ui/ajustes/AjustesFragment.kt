package com.example.soluemergencias.ui.ajustes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.R
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.databinding.FragmentAjustesBinding
import com.example.soluemergencias.utils.closeKeyboard
import com.example.soluemergencias.utils.showAlertWithStringResources
import com.example.soluemergencias.utils.showToastInMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AjustesFragment: Fragment() {
    private var _binding : FragmentAjustesBinding? = null
    private val _viewModel: AjustesViewModel by inject()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var usuario: UsuarioDBO
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        _binding!!.viewModel = _viewModel
        _binding!!.lifecycleOwner = this
        sharedPreferences = requireActivity().getSharedPreferences("dark_mode", Context.MODE_PRIVATE)

        lifecycleScope.launch(Dispatchers.IO){
            usuario = _viewModel.obtenerDatosDelUsuario()
            lifecycleScope.launch(Dispatchers.Main){
                _binding!!.editTextAjustesNombreCompleto.setText(usuario.nombreCompleto)
                _binding!!.editTextAjustesTelefono.setText(usuario.telefono)
                _binding!!.editTextAjustesPassword.setText(usuario.password)
            }
        }

        _binding!!.switchAjustesDarkMode.apply{
            this.isChecked = sharedPreferences.getBoolean("dark_mode", false)
            this.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked ) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPreferences.edit().putBoolean("dark_mode", true).apply()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPreferences.edit().putBoolean("dark_mode", false).apply()
                }
            }
        }
        _binding!!.buttonAjustesActualizarPerfil.setOnClickListener{
            val nombreCompleto = _binding!!.editTextAjustesNombreCompleto.text.toString()
            val telefono = _binding!!.editTextAjustesTelefono.text.toString()
            requireActivity().closeKeyboard(it)
            if(nombreCompleto == usuario.nombreCompleto && telefono == usuario.telefono){
                showToastInMainThread(requireContext(), R.string.actualizar_campos_check)
                return@setOnClickListener
            }
            requireActivity().showAlertWithStringResources(R.string.atencion, R.string.actualizar_datos_consulta){
                lifecycleScope.launch(Dispatchers.IO){
                    val task = _viewModel.actualizarDatosDelUsuario(nombreCompleto, telefono)
                    showToastInMainThread(requireContext(), task.second)
                }
            }

        }

        _binding!!.buttonAjustesActualizarPassword.setOnClickListener{
            val password = _binding!!.editTextAjustesPassword.text.toString()
            val confirmarPassword = _binding!!.editTextAjustesConfirmarPassword.text.toString()
            requireActivity().closeKeyboard(it)
            if(password.isEmpty() || confirmarPassword.isEmpty()){
                showToastInMainThread(requireContext(), R.string.actualizar_password_check)
                return@setOnClickListener
            }
            if(password == usuario.password){
                showToastInMainThread(requireContext(), R.string.actualizar_password_check)
                return@setOnClickListener
            }
            if(password != confirmarPassword){
                showToastInMainThread(requireContext(), R.string.password_no_coinciden)
                return@setOnClickListener
            }else{
                requireActivity().showAlertWithStringResources(R.string.atencion, R.string.actualizar_password_consulta){
                    lifecycleScope.launch(Dispatchers.IO){
                        val task = _viewModel.actualizarPassword(password)
                        showToastInMainThread(requireContext(), task.second)
                    }
                }
            }
        }
        return _binding!!.root

    }
}