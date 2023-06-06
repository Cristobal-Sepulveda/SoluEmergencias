package com.example.soluemergencias.data

import com.example.soluemergencias.data.data_objects.dbo.RutVinculadoDBO
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.*
import com.google.firebase.firestore.GeoPoint

interface AppDataSource {

    suspend fun obtenerUsuarioDesdeRoom(): UsuarioDBO
    suspend fun actualizarFotoDePerfilEnFirestoreYRoom(fotoPerfil: String): Pair<Boolean, Int>
    suspend fun crearCuentaEnFirebaseAuthYFirestore(dataUsuarioEnFirestore: DataUsuarioEnFirestore): Pair<Boolean, Int>
    suspend fun iniciarLoginYValidacionesConRut(rut: String): Pair<Boolean, Int>
    suspend fun sesionActivaAFalseYLogout(): Pair<Boolean, Int>
    suspend fun chequearSiHaySolicitudesDeVinculacionRecibidasSinGestionar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>
    suspend fun chequearSiHaySolicitudesDeVinculacionEnviadas(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>
    suspend fun crearSolicitudDeVinculo(rutAVincular: String): Pair<Boolean, Int>
    suspend fun aprobarORechazarSolicitudDeVinculo(
        rutSolicitante: String,
        boolean: Boolean
    ): Pair<Boolean, Int>
    suspend fun cargandoListaDeContactosDeEmergencia(): Triple<Boolean, Int, MutableList<ContactoDeEmergencia>>
    suspend fun crearContactoDeAsistencia(nombre: String, telefono: String): Pair<Boolean, Int>
    suspend fun recuperarClave(rut: String): Pair<Boolean, String>
    suspend fun enviarSugerencia(comentario: String): Pair<Boolean, Int>
    suspend fun actualizarDatosDelUsuario(
        nombreCompleto: String,
        telefono: String
    ): Pair<Boolean, Int>

    suspend fun actualizarPassword(password: String): Pair<Boolean, Int>
    suspend fun obtenerUsuarioVinculado(): Triple<Boolean, Int, DataUsuarioEnFirestore?>
    suspend fun desvincularUsuarios(): Pair<Boolean, Int>
    suspend fun registrarLlamadoDeEmergencia(llamadoDeEmergencia: LlamadoDeEmergencia): Pair<Boolean, Int>
    suspend fun cargandoRegistroDeActividadAsesorDelHogar(): Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?>
    suspend fun cargandoRegistroDeActividadDuenoDeCasa(): Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?>

    suspend fun registrarLocalizacionEnEmergencia(geoPoint: GeoPoint)

    suspend fun guardarRutVinculado(rutVinculadoDBO: RutVinculadoDBO)

    suspend fun obtenerRutVinculado(): RutVinculadoDBO
}