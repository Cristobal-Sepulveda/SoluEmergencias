package com.example.soluemergencias.data.data_objects.domainObjects

data class ContactoDeAsistencia(
    val rut: String,
    val nombre: String,
    val telefono: String
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "rut" to rut,
            "nombre" to nombre,
            "telefono" to telefono,
        )
    }
}