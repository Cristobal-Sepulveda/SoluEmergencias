package com.example.soluemergencias.data.data_objects.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class RutVinculadoDBO(
    val rutVinculado: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString()
)