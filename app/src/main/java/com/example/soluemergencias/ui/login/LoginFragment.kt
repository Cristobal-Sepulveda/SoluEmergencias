package com.example.soluemergencias.ui.login

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.MainActivity
import com.example.soluemergencias.R
import com.example.soluemergencias.databinding.FragmentLoginBinding
import com.example.soluemergencias.ui.base.BaseFragment
import com.example.soluemergencias.utils.Constants
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.example.soluemergencias.utils.NavigationCommand
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class LoginFragment: BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    override val _viewModel: LoginViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        Log.e("LoginFragment", "onCreateView")
        //Aquí chequeo si hay usuario logeado en firebase
        lifecycleScope.launch{
            withContext(Dispatchers.IO) {
                hayUsuarioLogeado(Constants.firebaseAuth.currentUser)
            }
        }

        _binding!!.textViewAuthenticationCrearCuenta.setOnClickListener{
            _viewModel.navigationCommand.value =
                NavigationCommand.To(LoginFragmentDirections
                    .actionNavigationLoginfragmentToNavigationCrearcuentafragment())
        }

        _binding!!.loginButton.setOnClickListener{
            iniciandoLogin()
        }

        return _binding?.root
    }

    private fun hayUsuarioLogeado(user: FirebaseUser?){
        //aparecerYDesaparecerElementosAlIniciarLogin()
        if (user != null) {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            requireActivity().finish()
            startActivity(intent)
        }else{
            //aparecerYDesaparecerElementosTrasNoLogin()
        }
    }

    private fun iniciandoLogin() {
        aparecerYDesaparecerElementosAlIniciarLogin()
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(_binding!!.edittextPassword.windowToken, 0)

        val email = _binding!!.edittextEmail.text.toString()
        val password = _binding!!.edittextPassword.text.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (email == "" || password == "") {
                    controlDeError(message = R.string.login_error_campos_vacios)
                    return@withContext
                }
                val listaDeUsuarios = _viewModel.obtenerUsuariosDesdeFirestore()

                if(listaDeUsuarios.isEmpty()){
                    controlDeError(message = R.string.login_error_no_hay_usuarios_en_firestore)
                }else{
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    var continuarConLogin = true
                                    listaDeUsuarios.forEach {
                                        if(it.id == firebaseAuth.currentUser!!.uid && it.sesionActiva){
                                            continuarConLogin = false
                                            controlDeError(message = R.string.sesion_activa_existente)
                                        }
                                    }
                                    if (continuarConLogin && _viewModel.sesionActivaATrueYLogin(requireContext())) {
                                        withContext(Dispatchers.Main) {
                                            _binding!!.progressBar.visibility = View.GONE
                                        }
                                        val intent = Intent(requireActivity(), MainActivity::class.java)
                                        requireActivity().finish()
                                        startActivity(intent)
                                    }else{
                                        withContext(Dispatchers.Main){
                                            Toast.makeText(requireContext(),
                                                R.string.sesion_activa_existente,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        FirebaseAuth.getInstance().signOut()
                                    }
                                }
                            }
                        }.addOnFailureListener{
                            controlDeError(exception = it, message = R.string.error_al_logear_en_firebaseauth)
                        }

                }
            }
        }
    }

    private fun controlDeError(exception: Exception? = null,  message: Int? = null) {
        if(exception == null){
            aparecerYDesaparecerElementosTrasNoLogin()

        }else{
            aparecerYDesaparecerElementosTrasNoLogin()
            lifecycleScope.launch{
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),
                        message!!,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun aparecerYDesaparecerElementosAlIniciarLogin() {
        _binding!!.progressBar.visibility = View.VISIBLE
        _binding!!.textViewAuthenticationTitulo.visibility = View.GONE
        _binding!!.imageviewLogoSoluEmergencias.visibility = View.GONE
        _binding!!.edittextEmail.visibility = View.GONE
        _binding!!.edittextPassword.visibility = View.GONE
        _binding!!.textViewAuthenticationClaveOlvidada.visibility = View.GONE
        _binding!!.textViewAuthenticationNecesitasUnaCuenta.visibility = View.GONE
        _binding!!.textViewAuthenticationCrearCuenta.visibility = View.GONE
        _binding!!.loginButton.visibility = View.GONE
    }

    private fun aparecerYDesaparecerElementosTrasNoLogin() {
        lifecycleScope.launch{
            withContext(Dispatchers.Main){
                _binding!!.progressBar.visibility = View.GONE
                _binding!!.textViewAuthenticationTitulo.visibility = View.VISIBLE
                _binding!!.imageviewLogoSoluEmergencias.visibility = View.VISIBLE
                _binding!!.edittextEmail.visibility = View.VISIBLE
                _binding!!.edittextPassword.visibility = View.VISIBLE
                _binding!!.textViewAuthenticationClaveOlvidada.visibility = View.VISIBLE
                _binding!!.textViewAuthenticationNecesitasUnaCuenta.visibility = View.VISIBLE
                _binding!!.textViewAuthenticationCrearCuenta.visibility = View.VISIBLE
                _binding!!.loginButton.visibility = View.VISIBLE
            }
        }
    }


}