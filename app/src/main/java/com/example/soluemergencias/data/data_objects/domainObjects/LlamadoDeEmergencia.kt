package com.example.soluemergencias.data.data_objects.domainObjects

import com.google.firebase.firestore.GeoPoint

data class LlamadoDeEmergencia(
    val rut: String,
    val fecha: String,
    val hora: String,
    val direccion: String,
    val motivoDelLlamado: String,
    val hogarDeLaEmergencia: String
){
    constructor(): this("", "", "", "", "", "")
    fun toMap(): Map<String,Any>{
        return mapOf(
            "rut" to rut,
            "fecha" to fecha,
            "hora" to hora,
            "direccion" to direccion,
            "motivoDelLlamado" to motivoDelLlamado,
            "hogarDeLaEmergencia" to hogarDeLaEmergencia
        )
    }
}
