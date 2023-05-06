package com.example.soluemergencias.data.data_objects.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class UsuarioDBO(
    val fotoPerfil: String,
    val nombreCompleto: String,
    val rut: String,
    val perfil: String,
    val telefono: String,
    val email: String,
    val password: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)