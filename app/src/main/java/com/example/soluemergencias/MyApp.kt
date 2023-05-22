package com.example.soluemergencias

import android.app.Application
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.AppRepository
import com.example.soluemergencias.data.app_database.getDatabase
import com.example.soluemergencias.ui.ajustes.AjustesViewModel
import com.example.soluemergencias.ui.crearcontactodeasistencia.CrearContactoDeAsistenciaViewModel
import com.example.soluemergencias.ui.crearcuenta.CrearCuentaViewModel
import com.example.soluemergencias.ui.llamadarealizada.LlamadaRealizadaViewModel
import com.example.soluemergencias.ui.login.LoginViewModel
import com.example.soluemergencias.ui.recuperarclave.RecuperarClaveViewModel
import com.example.soluemergencias.ui.sugerencias.SugerenciasViewModel
import com.example.soluemergencias.ui.vincularcuentas.VincularCuentasViewModel
import com.example.soluemergencias.ui.vistageneral.VistaGeneralViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if(sharedPreferences.getBoolean("dark_mode", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        /**
         * using Koin Library as a service locator
         */
        val myModule = module {
            //Declare singleton definitions to be later injected using by inject()
            single {
                VistaGeneralViewModel(get() as AppDataSource)
            }
            single {
                CrearCuentaViewModel(get() as AppDataSource)
            }
            single{
                LoginViewModel(get() as AppDataSource)
            }
            single{
                VincularCuentasViewModel(get() as AppDataSource)
            }
            single{
                CrearContactoDeAsistenciaViewModel(get() as AppDataSource)
            }
            single{
                RecuperarClaveViewModel(get() as AppDataSource)
            }
            single{
                SugerenciasViewModel(get() as AppDataSource)
            }
            single{
                AjustesViewModel(get() as AppDataSource)
            }
            single{
                LlamadaRealizadaViewModel(get() as AppDataSource)
            }

            single { getDatabase(this@MyApp).usuarioDao }
/*            single { getDatabase(this@MyApp).latLngYHoraActualDao }
            single { getDatabase(this@MyApp).jwtDao }*/

            //REPOSITORY
            single { AppRepository(applicationContext, get()) as AppDataSource }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }

/*        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager*/

        //delayedInit()

    }
}

/*    private fun delayedInit() {
        applicationScope.launch{
            setupRecurringWork_fieldUpdate()
            setupRecurringWork_calendarUpdate()
        }
    }

    private fun setupRecurringWork_fieldUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<updatingFIELD_DBO_IN_APP_DATABASE>(15, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            updatingFIELD_DBO_IN_APP_DATABASE.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }

    private fun setupRecurringWork_calendarUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<updatingCalendar_inAllFields_toNextDay_inCLOUDFIRESTORE>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            updatingCalendar_inAllFields_toNextDay_inCLOUDFIRESTORE.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }*/

