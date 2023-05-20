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
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var navController: NavController
    //private val cloudDB = FirebaseFirestore.getInstance()
    val dataSource: AppDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = findViewById<Toolbar>(R.id.toolbarAuthenticationActivity)
        setSupportActionBar(toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_authentication) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        checkingPermissionsSettings()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_authentication)
        return navController.navigateUp() || super.onSupportNavigateUp()
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

