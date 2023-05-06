package com.example.soluemergencias

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
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
import de.hdodenhof.circleimageview.CircleImageView
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
        pintandoSideBarMenuYBottomAppBarSegunElPerfilDelUsuario()
        
    }

    private fun pintandoSideBarMenuYBottomAppBarSegunElPerfilDelUsuario() {
        lifecycleScope.launch(Dispatchers.Main) {
            dataSource.obtenerUsuarioDesdeRoom().let {
                val nombre = it.nombreCompleto.split(" ")[0]
                val perfil = it.perfil
                Log.e("perfil", "${it.perfil}")
                val fotoPerfil = it.fotoPerfil
                if (it.perfil == "a") {
                    binding.navView.menu.findItem(R.id.navigation_crear_contacto_de_asistencia).isVisible = false
                    binding.navView.menu.findItem(R.id.navigation_contactos_de_asistencia).isVisible = false
                }
                binding.navView.getHeaderView(0)
                    .findViewById<TextView>(R.id.textView_drawerNavHeader_nombreUsuario)
                    .text = nombre
                binding.navView.getHeaderView(0)
                    .findViewById<TextView>(R.id.textView_drawerNavHeader_perfil)
                    .text = perfil
                decodeAndSetImageString(fotoPerfil)
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
        val task = dataSource.sesionActivaAFalseYLogout()
        if(!task.first){
            mostrarSnackBarEnMainThread(this@MainActivity, task.second)
        }else{
            firebaseAuth.signOut()
            this@MainActivity.finish()
            startActivity(Intent(this@MainActivity, AuthenticationActivity::class.java))
        }
    }

    private fun decodeAndSetImageString(fotoPerfil: String){
        val circleImageView = binding.navView.getHeaderView(0)
            .findViewById<CircleImageView>(R.id.circleImageView_drawerNavHeader_fotoPerfil)
        circleImageView.invalidate()
        if ((fotoPerfil.last().toString() == "=") || ((fotoPerfil.first().toString() == "/") && (fotoPerfil[1].toString() == "9"))) {
            val decodedString = Base64.decode(fotoPerfil, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            circleImageView.setImageBitmap(decodedByte)
        } else {
            val aux2 = fotoPerfil.indexOf("=") + 1
            val aux3 = fotoPerfil.substring(0, aux2)
            val decodedString = Base64.decode(aux3, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            circleImageView.setImageBitmap(decodedByte)
        }
    }
}
