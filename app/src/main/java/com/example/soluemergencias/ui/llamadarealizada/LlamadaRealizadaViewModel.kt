package com.example.soluemergencias.ui.llamadarealizada

import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergencia
import com.google.firebase.firestore.GeoPoint

class LlamadaRealizadaViewModel(val dataSource: AppDataSource): ViewModel() {

    suspend fun registrarLlamadoDeEmergencia(llamadoDeEmergencia: LlamadoDeEmergencia): Pair<Boolean, Int>{
        return dataSource.registrarLlamadoDeEmergencia(llamadoDeEmergencia)
    }

    suspend fun registrarLocalizacionEnEmergencia(geoPoint: GeoPoint) {
        dataSource.registrarLocalizacionEnEmergencia(geoPoint)
    }

}