package com.example.soluemergencias.ui.llamadarealizada

import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource

class LlamadaRealizadaViewModel(val dataSource: AppDataSource): ViewModel() {

    suspend fun guardarComentarioDeLaEmergencia(comentarios: String): Pair<Boolean, Int>{
        return dataSource.guardarComentarioDeLaEmergencia(comentarios)
    }

    suspend fun ignorarEmergencia(): Pair<Boolean, Int>{
        return dataSource.ignorarEmergencia()
    }
}