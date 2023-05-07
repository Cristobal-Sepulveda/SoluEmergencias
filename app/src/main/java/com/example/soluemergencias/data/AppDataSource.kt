package com.example.soluemergencias.data

import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo

interface AppDataSource {

    suspend fun obtenerUsuarioDesdeRoom(): UsuarioDBO
    suspend fun actualizarFotoDePerfilEnFirestoreYRoom(fotoPerfil: String): Pair<Boolean, Int>
    suspend fun crearCuentaEnFirebaseAuthYFirestore(dataUsuarioEnFirestore: DataUsuarioEnFirestore):Pair<Boolean, Int>
    suspend fun iniciarLoginYValidacionesConRut(rut:String): Pair<Boolean,Int>
    suspend fun sesionActivaAFalseYLogout():Pair<Boolean, Int>
    suspend fun chequearSiHaySolicitudesPorAprobar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>
    suspend fun crearSolicitudDeVinculo(rutAVincular: String): Pair<Boolean,Int>
    suspend fun aprobarORechazarSolicitudDeVinculo(rutSolicitante: String, boolean:Boolean):Pair<Boolean, Int>

    suspend fun cargandoListaDeContactosDeEmergencia(): Triple<Boolean, Int, MutableList<ContactoDeEmergencia>>

}