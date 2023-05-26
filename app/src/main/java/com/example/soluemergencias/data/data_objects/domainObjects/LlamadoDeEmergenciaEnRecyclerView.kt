package com.example.soluemergencias.data.data_objects.domainObjects

import com.google.firebase.firestore.GeoPoint

data class LlamadoDeEmergenciaEnRecyclerView(
    val rut: String,
    val fecha: String,
    val hora: String,
    val geoPoint: GeoPoint,
    val motivoDelLlamado: String,
    var estado: String,
    val hogarDeLaEmergencia: String,
    var id: String
){
    constructor(): this("", "", "", GeoPoint(0.0, 0.0), "", "","", "")
    fun toMap(): Map<String,Any>{
        return mapOf(
            "rut" to rut,
            "fecha" to fecha,
            "hora" to hora,
            "geoPoint" to geoPoint,
            "motivoDelLlamado" to motivoDelLlamado,
            "estado" to estado,
            "hogarDeLaEmergencia" to hogarDeLaEmergencia,
            "id" to id
        )
    }
}
