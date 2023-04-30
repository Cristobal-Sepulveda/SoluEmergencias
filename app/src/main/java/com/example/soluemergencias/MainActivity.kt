package com.example.soluemergencias

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.databinding.ActivityMainBinding
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), MenuProvider {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var menuHost: MenuHost = this
    private val dataSource: AppDataSource by inject()
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        menuHost.addMenuProvider(this, this, Lifecycle.State.RESUMED)
        rootView = binding.root


        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView.menu.findItem(R.id.logout_item).setOnMenuItemClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    launchLogoutFlow()
                }
            }
            true
        }

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.cerrarSesion -> {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        launchLogoutFlow()
                    }
                }
            }
            R.id.acercaDe -> {
                Toast.makeText(
                    this,
                    R.string.proxima_funcionalidad,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return false

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private suspend fun launchLogoutFlow() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        //aquí chequeo si hay internet
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
            logout()
        }else{
            Snackbar.make(
                binding.root,
                R.string.no_hay_internet,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private suspend fun logout() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if(dataSource.sesionActivaAFalseYLogout(this@MainActivity)){
                    firebaseAuth.signOut()
                    this@MainActivity.finish()
                    startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
                }
            }
        }
    }
}