package com.example.soluemergencias.utils
import android.os.Build
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth

object Constants {
    val firebaseAuth = FirebaseAuth.getInstance()
    const val backend_url = "https://backend-soluemergencias-smn5thn5kq-tl.a.run.app"
    //const val backend_url = "http://192.168.1.138:8080"
    enum class CloudRequestStatus{LOADING, ERROR, DONE, DONE_WITH_NO_DATA}
    val defaultLocation = LatLng(-33.47536870666403, -70.64367761577908)
    const val cameraDefaultZoom = 10.7

    var defaultContactosDeEmergencia = mutableListOf(
        ContactoDeEmergencia("0", "CONAF", "130", fotoCONAF,null),
        ContactoDeEmergencia("1","SAMU", "131", fotoSAMU,null),
        ContactoDeEmergencia("2", "Bomberos", "132", fotoBomberos,null),
        ContactoDeEmergencia("3","Carabineros", "133", fotoCarabineros,null),
        ContactoDeEmergencia("4","PDI", "134", fotoPDI,null),
    )
    private const val PACKAGE_NAME = "com.example.soluemergencias"
    const val ACTION_MAP_LOCATION_BROADCAST = "$PACKAGE_NAME.action.MAP_LOCATION_BROADCAST"
    internal const val ACTION_LOCATION_BROADCAST = "$PACKAGE_NAME.action.LOCATION_BROADCAST"
    internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
    const val NOTIFICATION_CHANNEL_ID = "localizacion"

}



