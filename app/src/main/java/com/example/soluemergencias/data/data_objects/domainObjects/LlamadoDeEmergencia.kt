package com.example.soluemergencias.data.data_objects.domainObjects

import com.google.firebase.firestore.GeoPoint

data class LlamadoDeEmergencia(
    val rut: String,
    val fecha: String,
    val hora: String,
    val geoPoint: GeoPoint,
    val motivoDelLlamado: String,
    val estado: String,
)
