package com.example.soluemergencias.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.soluemergencias.R
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.ACTION_LOCATION_BROADCAST
import com.example.soluemergencias.utils.Constants.ACTION_MAP_LOCATION_BROADCAST
import com.example.soluemergencias.utils.Constants.EXTRA_LOCATION
import com.example.soluemergencias.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.google.android.gms.location.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class LocationService : Service() {

    private val dataSource: AppDataSource by inject()

    /*
     * Checks whether the bound activity has really gone away (foreground service with notification
     * created) or simply orientation change (no-op).
     */
    private var configurationChange = false

    private var serviceRunningInForeground = false
    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }
    private val localBinder = LocalBinder()
    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private lateinit var mapFragmentLocationCallback: LocationCallback
    private var currentLocation: Location? = null

    private var timeInterval: Long = TimeUnit.SECONDS.toMillis(15)


    override fun onCreate() {
        Log.e("LocationService", "onCreate()")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval)
            .apply {
                setMinUpdateIntervalMillis(TimeUnit.SECONDS.toMillis(14))
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

         /*Called from MapFragment when FusedLocationProviderClient has a new Location. */
        mapFragmentLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult.lastLocation
                val intent = Intent(ACTION_MAP_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun subscribeToLocationUpdates() {
        Log.e("LocationService", "subscribeToLocationUpdatesMapFragment()")
        try{
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                mapFragmentLocationCallback,
                Looper.getMainLooper()
            )
            startForegroundService(Intent(applicationContext, LocationService::class.java))
        }catch(e:Exception){
            Log.e("LocationService", "subscribeToLocationUpdatesMapFragment() error: ${e.message}")
        }
    }

    fun unsubscribeToLocationUpdates(){
        Log.e("LocationService", "unsubscribeToLocationUpdatesMapFragment()")
        try {
            fusedLocationProviderClient.removeLocationUpdates(mapFragmentLocationCallback)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e("LocationService", "unsubscribeToLocationUpdatesMapFragment() error: ${unlikely.message}")
        }
    }


    private fun generateNotification(mainText: Int): Notification {
        val mainNotificationText = getString(mainText)
        val titleText = getString(R.string.app_name)
        val notificationCompatBuilder = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(notificationChannel)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            //.setSmallIcon(R.mipmap.icono_app_foreground)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            //.setContentIntent(activityPendingIntent)
            .build()
    }

    //Called by the system every time a client explicitly starts the service by calling startForegroundService(Intent),
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("LocationService", "onStartCommand()")
        startForeground(1, generateNotification(R.string.servicio_rastreo_iniciado))
        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        configurationChange = true
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e("LocationService", "onBind()")
        // MainActivity (client) comes into foreground and binds to service, so the service can
        // become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.e("LocationService", "onRebind()")
        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.e("LocationService", "onUnbind()")
        // MainActivity (client) leaves foreground, so service needs to become a foreground service
        // to maintain the 'while-in-use' label.
        // NOTE: If this method is called due to a configuration change in MainActivity,
        // we do nothing.
        if (!configurationChange) {
            Log.e("LocationService", "Start foreground service")
            serviceRunningInForeground = true
        }

        // Ensures onRebind() is called if MainActivity (client) rebinds.
        return true
    }

}
