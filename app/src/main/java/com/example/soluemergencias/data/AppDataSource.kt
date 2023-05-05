package com.example.soluemergencias.data

import android.content.Context
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.PreDataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo

interface AppDataSource {
    /*suspend fun eliminarTokeanDeSesion()
    suspend fun validarTokenDeSesion(): Boolean*/
    suspend fun crearCuentaEnFirebaseAuthYFirestore(preDataUsuarioEnFirestore: PreDataUsuarioEnFirestore):Boolean
    suspend fun iniciarLoginYValidacionesConRut(rut:String): Boolean
    suspend fun sesionActivaAFalseYLogout(context:Context):Boolean
    suspend fun chequearSiHaySolicitudesPorAprobar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>
    suspend fun crearSolicitudDeVinculo(rutAVincular: String): Pair<Boolean,Int>
}