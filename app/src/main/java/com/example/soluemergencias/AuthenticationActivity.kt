package com.example.soluemergencias

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.example.soluemergencias.databinding.ActivityAuthenticationBinding
import com.example.soluemergencias.utils.showAlert
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
        checkingPermissionsSettings()

    }



    private fun checkingPermissionsSettings(resolve: Boolean = true) {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()) { results ->

                for (result in results) {
                    if (!result.value && result.key == Manifest.permission.ACCESS_FINE_LOCATION) {
                        Snackbar.make(
                            binding.root,
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(R.string.settings) {
                            startActivityForResult(Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", packageName, null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }, 1001)
                        }.show()
                        break
                    }
                }
            }
            val x = R.string.permiso_de_ubicacion
            if (permissionsToRequest.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.showAlert(R.string.permiso_de_ubicacion, R.string.aviso_de_permiso) {
                    requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
            } else {
                requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }


}

