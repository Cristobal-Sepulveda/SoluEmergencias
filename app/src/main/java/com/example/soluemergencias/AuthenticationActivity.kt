package com.example.soluemergencias

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.example.soluemergencias.databinding.ActivityAuthenticationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    //private val cloudDB = FirebaseFirestore.getInstance()
    val dataSource: AppDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        //Aquí chequeo si hay usuario logeado en firebase
        lifecycleScope.launch{
            withContext(Dispatchers.IO) {
                hayUsuarioLogeado(firebaseAuth.currentUser)
            }
        }

        binding.loginButton.setOnClickListener {
            launchSignInFlow()
        }

    }

    private fun hayUsuarioLogeado(user: FirebaseUser?){
        aparecerYDesaparecerElementosAlIniciarLogin()
        if (user != null) {
            val intent = Intent(this@AuthenticationActivity, MainActivity::class.java)
            finish()
            startActivity(intent)
        }else{
            aparecerYDesaparecerElementosTrasNoLogin()
        }
    }

    private fun launchSignInFlow() {
        runOnUiThread {
            aparecerYDesaparecerElementosAlIniciarLogin()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.edittextPassword.windowToken, 0)
        }

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        //aquí chequeo si hay internet
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
            val email = binding.edittextEmail.text.toString()
            val password = binding.edittextPassword.text.toString()
            preIntentarLogin(email, password)
        } else {
            runOnUiThread {
                aparecerYDesaparecerElementosTrasNoLogin()

                Snackbar.make(
                    findViewById(R.id.container),
                    R.string.no_hay_internet,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun preIntentarLogin(email: String, password: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                //primer error controlado: Chequear si el ambos input tienen valores, se dejo aqui por limpieza del codigo
                if(email =="" || password ==""){
                    controlDeError(message = R.string.login_error_campos_vacios )
                    return@withContext
                }
                firebaseAuth.signInWithEmailAndPassword(email, password)
                val user = FirebaseAuth.getInstance().currentUser
                val name = user?.displayName?.split(" ")?.get(0) // get first name
                val lastName = user?.displayName?.split(" ")?.get(1) // get last name
                val completeName = name + lastName
                Log.d("AuthenticationActivity", "Name: $name")
                Log.d("AuthenticationActivity", "Last Name: $lastName")
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        iniciandoLogin(completeName)
                    }
                }
            }
        }
    }


    private suspend fun iniciandoLogin(completeName: String) {
        if(completeName.isEmpty()){
            controlDeError(message = R.string.login_error_credenciales_incorrectas )
            return
        }
        lifecycleScope.launch{
            withContext(Dispatchers.IO){
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    val intent = Intent(this@AuthenticationActivity, MainActivity::class.java)
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                        }
                    }
                    finish()
                    startActivity(intent)
                }
            }
        }
    }


    private fun controlDeError(exception: Exception? = null,  message: Int? = null) {
        if(exception == null){
            runOnUiThread {
                aparecerYDesaparecerElementosTrasNoLogin()
                Snackbar.make(
                    findViewById(R.id.container),
                    message!!,
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        }else{
            runOnUiThread {
                aparecerYDesaparecerElementosTrasNoLogin()
                Snackbar.make(
                    findViewById(R.id.container),
                    "Error: ${exception.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun aparecerYDesaparecerElementosTrasNoLogin() {
        binding.progressBar.visibility = View.GONE
        binding.textViewAuthenticationTitulo.visibility = View.VISIBLE
        binding.imageviewLogoSoluEmergencias.visibility = View.VISIBLE
        binding.edittextEmail.visibility = View.VISIBLE
        binding.edittextPassword.visibility = View.VISIBLE
        binding.loginButton.visibility = View.VISIBLE
    }


    private fun aparecerYDesaparecerElementosAlIniciarLogin() {
        binding.progressBar.visibility = View.VISIBLE
        binding.textViewAuthenticationTitulo.visibility = View.GONE
        binding.imageviewLogoSoluEmergencias.visibility = View.GONE
        binding.edittextEmail.visibility = View.GONE
        binding.edittextPassword.visibility = View.GONE
        binding.loginButton.visibility = View.GONE
    }

}

