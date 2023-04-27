package com.example.soluemergencias

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.databinding.ActivityAuthenticationBinding
import com.example.soluemergencias.utils.Constants.shouldIAskForNotificationPermissions
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



    private fun checkingPermissionsSettings() {
        val shouldIAskForNotificationPermissions = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            shouldIAskForNotificationPermissions
        )
        val checkingPermissions = permissions.filter {
            it?.let { it1 -> checkSelfPermission(it1) } == PackageManager.PERMISSION_DENIED
        }

        requestPermissions(checkingPermissions.toTypedArray(), 0)
    }
}

