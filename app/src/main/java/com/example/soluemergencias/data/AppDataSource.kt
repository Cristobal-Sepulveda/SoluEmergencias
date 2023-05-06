package com.example.soluemergencias.data

import android.content.Context
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo

interface AppDataSource {
    /*suspend fun eliminarTokeanDeSesion()
    suspend fun validarTokenDeSesion(): Boolean*/
    suspend fun crearCuentaEnFirebaseAuthYFirestore(dataUsuarioEnFirestore: DataUsuarioEnFirestore):Pair<Boolean, Int>
    suspend fun iniciarLoginYValidacionesConRut(rut:String): Pair<Boolean,Int>
    suspend fun sesionActivaAFalseYLogout():Pair<Boolean, Int>
    suspend fun chequearSiHaySolicitudesPorAprobar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>
    suspend fun crearSolicitudDeVinculo(rutAVincular: String): Pair<Boolean,Int>
}