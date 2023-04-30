package com.example.soluemergencias.ui.login

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.soluemergencias.MainActivity
import com.example.soluemergencias.R
import com.example.soluemergencias.databinding.FragmentLoginBinding
import com.example.soluemergencias.ui.base.BaseFragment
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LoginFragment: BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    override val _viewModel: LoginViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        hayUsuarioLogeado(firebaseAuth.currentUser)

        _binding!!.textViewAuthenticationCrearCuenta.setOnClickListener{
            findNavController().navigate(LoginFragmentDirections
                .actionNavigationLoginfragmentToNavigationCrearcuentafragment()
            )
        }

        _binding!!.loginButton.setOnClickListener{
            lifecycleScope.launch(Dispatchers.IO){
                iniciandoLogin()
            }
        }

        return _binding!!.root
    }

    private fun hayUsuarioLogeado(user: FirebaseUser?){
        aparecerYDesaparecerElementosAlIniciarLogin()
        if (user != null) {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            requireActivity().finish()
            startActivity(intent)
        }else{
            aparecerYDesaparecerElementosTrasNoLogin()
        }
    }
    private suspend fun iniciandoLogin() {
        aparecerYDesaparecerElementosAlIniciarLogin()
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(_binding!!.edittextPassword.windowToken, 0)
        val rut = _binding!!.edittextRut.text.toString()
        val password = _binding!!.edittextPassword.text.toString()
        if (rut == "" || password == ""){
            aparecerYDesaparecerElementosTrasNoLogin()
            lifecycleScope.launch(Dispatchers.Main){
                Toast.makeText(requireContext(),
                    R.string.login_error_campos_vacios,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if(!_viewModel.iniciarLoginYValidacionesConRut(rut)){
            Log.e("LoginFragmentt", "iniciandoLogin: ${_viewModel.status.value}")
            aparecerYDesaparecerElementosTrasNoLogin()
        }else{
            Log.e("LoginFragment", "iniciandoLogin: ${_viewModel.status.value}")
            val intent = Intent(requireActivity(), MainActivity::class.java)
            requireActivity().finish()
            startActivity(intent)
        }
    }

    private fun aparecerYDesaparecerElementosAlIniciarLogin() {
        lifecycleScope.launch(Dispatchers.Main) {
            _binding!!.apply {
                progressBar.visibility = View.VISIBLE
                textViewAuthenticationTitulo.visibility = View.GONE
                imageviewLogoSoluEmergencias.visibility = View.GONE
                edittextRut.visibility = View.GONE
                edittextPassword.visibility = View.GONE
                textViewAuthenticationClaveOlvidada.visibility = View.GONE
                textViewAuthenticationNecesitasUnaCuenta.visibility = View.GONE
                textViewAuthenticationCrearCuenta.visibility = View.GONE
                loginButton.visibility = View.GONE
            }
        }
    }
    private fun aparecerYDesaparecerElementosTrasNoLogin() {
        lifecycleScope.launch(Dispatchers.Main){
            _binding!!.apply{
                progressBar.visibility = View.GONE
                textViewAuthenticationTitulo.visibility = View.VISIBLE
                imageviewLogoSoluEmergencias.visibility = View.VISIBLE
                edittextRut.visibility = View.VISIBLE
                edittextPassword.visibility = View.VISIBLE
                textViewAuthenticationClaveOlvidada.visibility = View.VISIBLE
                textViewAuthenticationNecesitasUnaCuenta.visibility = View.VISIBLE
                textViewAuthenticationCrearCuenta.visibility = View.VISIBLE
                loginButton.visibility = View.VISIBLE
            }
        }
    }

}