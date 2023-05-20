package com.example.soluemergencias.utils
import android.os.Build
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.google.firebase.auth.FirebaseAuth

object Constants {
    val firebaseAuth = FirebaseAuth.getInstance()
    const val backend_url = "https://backend-soluemergencias-smn5thn5kq-tl.a.run.app"
    //const val backend_url = "http://192.168.1.138:8080"
    enum class CloudRequestStatus{LOADING, ERROR, DONE, DONE_WITH_NO_DATA}

    var defaultContactosDeEmergencia = mutableListOf(
        ContactoDeEmergencia("0", "CONAF", "130", fotoCONAF,null),
        ContactoDeEmergencia("1","SAMU", "131", fotoSAMU,null),
        ContactoDeEmergencia("2", "Bomberos", "132", fotoBomberos,null),
        ContactoDeEmergencia("3","Carabineros", "133", fotoCarabineros,null),
        ContactoDeEmergencia("4","PDI", "134", fotoPDI,null),
    )

}



