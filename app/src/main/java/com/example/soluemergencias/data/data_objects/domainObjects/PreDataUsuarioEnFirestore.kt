package com.example.soluemergencias.data.data_objects.domainObjects


data class PreDataUsuarioEnFirestore(
    val fotoPerfil: String,
    val nombreCompleto: String,
    val rut: String,
    val telefono: String,
    val email: String,
    val password: String,
    val perfil: String,
)