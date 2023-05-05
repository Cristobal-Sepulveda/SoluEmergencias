package com.example.soluemergencias

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
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
import com.example.soluemergencias.utils.mostrarSnackBarEnMainThread
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), MenuProvider {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var rootView: View
    private var menuHost: MenuHost = this
    private val dataSource: AppDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        menuHost.addMenuProvider(this, this, Lifecycle.State.RESUMED)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setupWithNavController(navController)
        rootView = binding.root
        binding.navView.menu.apply{
            this.findItem(R.id.logout_item).setOnMenuItemClickListener {
                lifecycleScope.launch(Dispatchers.IO) { logout() }
                true
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val drawerLayout = binding.drawerLayout
        NavigationUI.navigateUp(navController, drawerLayout)
        return true
    }
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.overflow_menu, menu)
    }
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.acercaDe -> {
                showToastInMainThreadWithStringResource(this, R.string.proxima_funcionalidad)
            }
        }
        return false
    }
    private suspend fun logout() {
        lifecycleScope.launch(Dispatchers.IO) {
            if(!dataSource.sesionActivaAFalseYLogout(this@MainActivity)){
                mostrarSnackBarEnMainThread(this@MainActivity, R.string.error_logout)
            }else{
                firebaseAuth.signOut()
                this@MainActivity.finish()
                startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
            }
        }
    }
}
