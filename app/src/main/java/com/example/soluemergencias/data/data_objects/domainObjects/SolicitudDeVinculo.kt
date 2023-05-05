package com.example.soluemergencias.data.data_objects.domainObjects

data class SolicitudDeVinculo(
    val rutDelSolicitante: String,
    val nombreDelSolicitante: String,
    val rutDelReceptor: String,
    val fechaDeSolicitud: String,
    val horaDeSolicitud: String,
    val solicitudAprobada: Boolean,
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "rutDelSolicitante" to rutDelSolicitante,
            "nombreDelSolicitante" to nombreDelSolicitante,
            "rutDelReceptor" to rutDelReceptor,
            "fechaDeSolicitud" to fechaDeSolicitud,
            "horaDeSolicitud" to horaDeSolicitud,
            "solicitudAprobada" to solicitudAprobada,
        )
    }
}