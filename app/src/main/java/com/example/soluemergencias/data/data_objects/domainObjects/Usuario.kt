package com.example.soluemergencias.data.data_objects.domainObjects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.Base64

@Parcelize
data class Usuario(
    val id: String,
    val fotoPerfil: String,
    val nombreCompleto: String,
    val telefono: String,
    val usuario: String,
    val password: String,
    var sesionActiva: Boolean,
    val perfil: String,
) : Parcelable{
    fun longConverterToString(id: Long): String{
        return id.toString()
    }
}