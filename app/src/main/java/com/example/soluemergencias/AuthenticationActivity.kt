package com.example.soluemergencias

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.databinding.ActivityAuthenticationBinding
import org.koin.android.ext.android.inject
import android.Manifest

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

    private fun checkingPermissionsSettings() {
        val permissions = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissions.plus(Manifest.permission.POST_NOTIFICATIONS)
        val checkingPermissions = permissions.filter {
            checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
        }
        if (checkingPermissions.isNotEmpty()) {
            requestPermissions(checkingPermissions.toTypedArray(), 0)
        }
    }
}

