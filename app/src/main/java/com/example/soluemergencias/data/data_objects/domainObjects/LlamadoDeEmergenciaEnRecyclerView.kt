package com.example.soluemergencias.data.data_objects.domainObjects

import com.google.firebase.firestore.GeoPoint

data class LlamadoDeEmergenciaEnRecyclerView(
    val rut: String,
    val fecha: String,
    val hora: String,
    val direccion: String,
    val motivoDelLlamado: String,
    val hogarDeLaEmergencia: String,
    var id: String
){
    constructor(): this("", "", "", "", "", "","")
    fun toMap(): Map<String,Any>{
        return mapOf(
            "rut" to rut,
            "fecha" to fecha,
            "hora" to hora,
            "direccion" to direccion,
            "motivoDelLlamado" to motivoDelLlamado,
            "hogarDeLaEmergencia" to hogarDeLaEmergencia,
            "id" to id
        )
    }
}
