package com.example.soluemergencias.data.data_objects.domainObjects


data class DataUsuarioEnFirestore(
    val fotoPerfil: String,
    val nombreCompleto: String,
    val rut: String,
    val telefono: String,
    val email: String,
    val password: String,
    val perfil: String,
    val sesionActiva: Boolean = false
){
    fun toMap(): Map<String, Any> {
        return mapOf(
            "fotoPerfil" to fotoPerfil,
            "nombreCompleto" to nombreCompleto,
            "rut" to rut,
            "telefono" to telefono,
            "email" to email,
            "password" to password,
            "perfil" to perfil,
            "sesionActiva" to sesionActiva,
        )
    }
}