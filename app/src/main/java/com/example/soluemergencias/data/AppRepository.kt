package com.example.soluemergencias.data

import android.content.Context
import android.util.Log
import com.example.soluemergencias.R
import com.example.soluemergencias.data.apiservices.RecuperarClaveApi
import com.example.soluemergencias.data.daos.RutVinculadoDao
import com.example.soluemergencias.data.daos.UsuarioDao
import com.example.soluemergencias.data.data_objects.dbo.RutVinculadoDBO
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.*
import com.example.soluemergencias.data.data_objects.dto.ApiResponse
import com.example.soluemergencias.utils.Constants.defaultContactosDeEmergencia
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.example.soluemergencias.utils.fotoDuenoDeCasa
import com.example.soluemergencias.utils.gettingLocalCurrentDateAndHour
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import java.util.*

@Suppress("LABEL_NAME_CLASH")
class AppRepository(private val context: Context,
                    private val usuarioDao: UsuarioDao,
                    private val rutVinculadoDao: RutVinculadoDao,
                    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): AppDataSource {

    private val cloudDB = FirebaseFirestore.getInstance()

    override suspend fun obtenerUsuarioDesdeRoom(): UsuarioDBO = withContext(ioDispatcher) {
        return@withContext usuarioDao.obtenerUsuarios()[0]
    }
    override suspend fun actualizarFotoDePerfilEnFirestoreYRoom(fotoPerfil: String):Pair<Boolean, Int> = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean, Int>>()
            cloudDB.collection("Usuarios")
                .whereEqualTo("rut", obtenerUsuarioDesdeRoom().rut)
                .get()
                .addOnFailureListener{
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener {
                    if(it.isEmpty){
                        deferred.complete(Pair(false, R.string.error_cloud_request))
                    }else{
                        cloudDB.collection("Usuarios")
                            .document(it.documents[0].id)
                            .update("fotoPerfil", fotoPerfil)
                            .addOnFailureListener{
                                deferred.complete(Pair(false, R.string.error_cloud_request))
                            }
                            .addOnSuccessListener{
                                CoroutineScope(Dispatchers.IO).launch {
                                    usuarioDao.actualizarFotoPerfil(fotoPerfil)
                                    deferred.complete(Pair(true, R.string.exito))
                                }
                            }
                    }

                }
            return@withContext deferred.await()
        }
    }
    override suspend fun crearCuentaEnFirebaseAuthYFirestore(dataUsuarioEnFirestore: DataUsuarioEnFirestore): Pair<Boolean, Int> = withContext(ioDispatcher) {
        val deferred = CompletableDeferred<Pair<Boolean, Int>>()
        val nuevoUsuario = DataUsuarioEnFirestore(
            dataUsuarioEnFirestore.fotoPerfil,
            dataUsuarioEnFirestore.nombreCompleto,
            dataUsuarioEnFirestore.rut,
            dataUsuarioEnFirestore.telefono,
            dataUsuarioEnFirestore.email,
            dataUsuarioEnFirestore.password,
            dataUsuarioEnFirestore.perfil
        )
        cloudDB.collection("Usuarios")
            .whereEqualTo("rut", nuevoUsuario.rut)
            .get()
            .addOnFailureListener{ deferred.complete(
                Pair(false, R.string.error_cloud_request)
            )
            }.addOnSuccessListener {
                if(!it.isEmpty){
                    deferred.complete(Pair(true, R.string.rut_ya_esta_en_uso))
                }else{
                    cloudDB.collection("Usuarios")
                        .add(nuevoUsuario.toMap())
                        .addOnFailureListener {
                            Log.e("asd", "asd")
                            deferred.complete(Pair(false, R.string.error_al_crear_usuario_en_firestore))
                        }
                        .addOnSuccessListener {
                            firebaseAuth.createUserWithEmailAndPassword(nuevoUsuario.email,
                                nuevoUsuario.password
                            ).addOnFailureListener{
                                if(it.message!!.contains("email address is already in use")){
                                    deferred.complete(Pair(true, R.string.email_en_uso))
                                }else{
                                    deferred.complete(Pair(false, R.string.error_al_crear_usuario_en_firebase_auth))
                                }
                            }.addOnSuccessListener {
                                    firebaseAuth.signOut()
                                    deferred.complete(Pair(true, R.string.usuario_creado_con_exito))
                            }
                        }


                }
            }
        return@withContext deferred.await()
    }
    override suspend fun iniciarLoginYValidacionesConRut(rut: String): Pair<Boolean,Int> = withContext(ioDispatcher) {
        withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Pair<Boolean, Int>>()
            cloudDB.collection("Usuarios")
                .whereEqualTo("rut", rut)
                .get()
                .addOnFailureListener {
                    deferred.complete(Pair(false, R.string.error_al_obtener_usuarios_en_firestore))
                }
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        deferred.complete(Pair(false, R.string.usuario_o_contraseña_erroneos))
                    } else {
                        val documentoDeInteres = it.documents[0]
                        if (documentoDeInteres["sesionActiva"] as Boolean) {
                            deferred.complete(Pair(false, R.string.usuario_ya_tiene_sesion_activa))
                        } else {
                            firebaseAuth.signInWithEmailAndPassword(
                                documentoDeInteres["email"] as String,
                                documentoDeInteres["password"] as String
                            ).addOnFailureListener {
                                deferred.complete(
                                    Pair(false, R.string.error_al_iniciar_sesion_en_firebase_auth)
                                )
                            }.addOnSuccessListener {
                                cloudDB.collection("Usuarios")
                                    .document(documentoDeInteres.id)
                                    .update("sesionActiva", true)
                                    .addOnFailureListener {
                                        firebaseAuth.signOut()
                                        deferred.complete(Pair(false, R.string.error_cloud_request))
                                    }
                                    .addOnSuccessListener {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            usuarioDao.guardarUsuario(
                                                UsuarioDBO(
                                                    documentoDeInteres["fotoPerfil"] as String,
                                                    documentoDeInteres["nombreCompleto"] as String,
                                                    documentoDeInteres["rut"] as String,
                                                    documentoDeInteres["perfil"] as String,
                                                    documentoDeInteres["telefono"] as String,
                                                    documentoDeInteres["email"] as String,
                                                    documentoDeInteres["password"] as String,
                                                )
                                            )
                                        }
                                        deferred.complete(Pair(true, R.string.exito))
                                    }
                            }
                        }
                    }
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun sesionActivaAFalseYLogout(): Pair<Boolean, Int> = withContext(ioDispatcher){
        withContext(ioDispatcher) {
            val deferred = CompletableDeferred<Pair<Boolean, Int>>()
            cloudDB.collection("Usuarios")
                .whereEqualTo("rut", usuarioDao.obtenerUsuarios()[0].rut)
                .get()
                .addOnFailureListener{
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener {
                    if(it.isEmpty){
                        deferred.complete(Pair(false, R.string.error_cloud_request))
                    }else{
                        val documentoDeInteres = it.documents[0]
                        cloudDB.collection("Usuarios")
                            .document(documentoDeInteres.id)
                            .update("sesionActiva", false)
                            .addOnFailureListener{
                                deferred.complete(Pair(false, R.string.error_cloud_request))
                            }
                            .addOnSuccessListener {
                                firebaseAuth.signOut()
                                CoroutineScope(Dispatchers.IO).launch{
                                    usuarioDao.eliminarUsuarios()
                                }
                                deferred.complete(Pair(true, R.string.exito))
                            }
                    }
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun chequearSiHaySolicitudesDeVinculacionRecibidasSinGestionar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>> = withContext(ioDispatcher) {
        withContext(Dispatchers.IO){
            val rut = usuarioDao.obtenerUsuarios()[0].rut
            val deferred = CompletableDeferred<Triple<Boolean, Int,
                    MutableList<SolicitudDeVinculo>>>()

            cloudDB.collection("SolicitudesDeVinculacion")
                .whereEqualTo("rutDelReceptor", rut)
                .whereEqualTo("solicitudGestionada", false)
                .get()
                .addOnFailureListener {
                    deferred.complete(Triple(false, R.string.error_cloud_request, mutableListOf()))
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Triple(true, R.string.no_hay_solicitudes_por_aprobar, mutableListOf()))
                    }else{
                        deferred.complete(Triple(true, R.string.exito, it.toObjects(SolicitudDeVinculo::class.java)))
                    }
                }

            deferred.await()
        }
    }
    override suspend fun chequearSiHaySolicitudesDeVinculacionEnviadas(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>> = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val rut = usuarioDao.obtenerUsuarios()[0].rut
            val deferred = CompletableDeferred<Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>>()

            cloudDB.collection("SolicitudesDeVinculacion")
                .whereEqualTo("rutDelSolicitante", rut)
                .get()
                .addOnFailureListener {
                    deferred.complete(Triple(false, R.string.error_cloud_request, mutableListOf()))
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Triple(true, R.string.tus_solicitudes_enviadas_han_sido_gestionadas, mutableListOf()))
                    }else{
                        deferred.complete(Triple(true, R.string.exito, it.toObjects(SolicitudDeVinculo::class.java)))
                    }
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun crearSolicitudDeVinculo(rutAVincular: String): Pair<Boolean,Int> = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean,Int>>()
            val (_,nombreCompleto, rut, perfil) = usuarioDao.obtenerUsuarios()[0]
            val perfilDelReceptor = when(perfil){
                "Dueño de casa" -> "Asesora del Hogar"
                "Asesora del Hogar" -> "Dueño de casa"
                else -> ""
            }
            val (date, hour) = gettingLocalCurrentDateAndHour()
            val solicitudDeVinculoMap = SolicitudDeVinculo(rut, nombreCompleto,
                rutAVincular, date, hour, false,false).toMap()

            cloudDB.collection("Usuarios")
                .whereEqualTo("rut", rutAVincular)
                .whereEqualTo("perfil", perfilDelReceptor)
                .get()
                .addOnFailureListener {
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener {
                    if(it.isEmpty){
                        when(perfil){
                            "Dueño de casa" -> deferred.complete(Pair(true, R.string.usuario_asesora_no_existe))
                            "Asesora del Hogar" -> deferred.complete(Pair(true, R.string.usuario_dueñodecasa_no_existe))
                        }
                    }else{
                        cloudDB.collection("SolicitudesDeVinculacion")
                            .whereEqualTo("rutDelSolicitante", rut)
                            .whereEqualTo("rutDelReceptor", rutAVincular)
                            .whereEqualTo("solicitudGestionada", false)
                            .get()
                            .addOnFailureListener {
                                deferred.complete(Pair(false, R.string.error_cloud_request))
                            }
                            .addOnSuccessListener {
                                if(!it.isEmpty){
                                    deferred.complete(Pair(true, R.string.solicitud_ya_existente))
                                }else{
                                    cloudDB.collection("SolicitudesDeVinculacion")
                                        .add(solicitudDeVinculoMap)
                                        .addOnFailureListener {
                                            deferred.complete(Pair(false, R.string.error_crear_solicitud))
                                        }
                                        .addOnSuccessListener {
                                            deferred.complete(Pair(true,R.string.solicitud_creada))
                                        }
                                }
                            }
                    }
                }

            return@withContext deferred.await()
        }
    }
    /*Función que modifica los atributos
            solicitudAprobada", boolean,
            "solicitudGestionada", true
            en el documento en la colección SolicitudesDeVinculación

       Si solicitudAprobada es true, la función edita el atributo
            rutVinculado en los documentos en la colección Usuarios
            que tengan rut ="rutDelSolicitante" o rut="rutDelReceptor"
            según corresponda*/
    override suspend fun aprobarORechazarSolicitudDeVinculo(
        rutSolicitante: String,
        boolean:Boolean): Pair<Boolean, Int> = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val currentUser = usuarioDao.obtenerUsuarios()[0]
            val deferred = CompletableDeferred<Pair<Boolean,Int>>()

            /*Aquí busco las solicitudes que me han enviado*/
            cloudDB.collection("SolicitudesDeVinculacion")
                .whereEqualTo("rutDelSolicitante", rutSolicitante)
                .whereEqualTo("rutDelReceptor", currentUser.rut)
                .whereEqualTo("solicitudGestionada", false)
                .get()
                .addOnFailureListener{
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Pair(false, R.string.error_cloud_request))
                    }else{
                        /**/
                        cloudDB.collection("SolicitudesDeVinculacion")
                            .document(it.documents[0].id)
                            .update("solicitudAprobada", boolean, "solicitudGestionada", true)
                            .addOnFailureListener{ deferred.complete(Pair(false, R.string.error_cloud_request)) }
                            .addOnSuccessListener {
                                if(boolean){
                                    cloudDB.collection("Usuarios")
                                        .whereEqualTo("rut", rutSolicitante)
                                        .get()
                                        .addOnFailureListener{ deferred.complete(Pair(false, R.string.error_cloud_request)) }
                                        .addOnSuccessListener{
                                            if(it.isEmpty){
                                                deferred.complete(Pair(false, R.string.error_cloud_request))
                                            }else{

                                                cloudDB.collection("Usuarios")
                                                    .document(it.documents[0].id)
                                                    .update("rutVinculado", currentUser.rut)
                                                    .addOnFailureListener { deferred.complete(Pair(false, R.string.error_cloud_request)) }
                                                    .addOnSuccessListener {
                                                        cloudDB.collection("Usuarios")
                                                            .whereEqualTo("rut", currentUser.rut)
                                                            .get()
                                                            .addOnFailureListener {
                                                                deferred.complete(Pair(false, R.string.error_cloud_request))
                                                            }
                                                            .addOnSuccessListener{
                                                                if(it.isEmpty) {
                                                                    deferred.complete(Pair(false, R.string.error_cloud_request))
                                                                }else{
                                                                    cloudDB.collection("Usuarios")
                                                                        .document(it.documents[0].id)
                                                                        .update("rutVinculado", rutSolicitante)
                                                                        .addOnFailureListener { deferred.complete(Pair(false, R.string.error_cloud_request)) }
                                                                        .addOnSuccessListener{
                                                                            when(boolean){
                                                                                true -> deferred.complete(Pair(true, R.string.solicitud_aprobada))
                                                                                false -> deferred.complete(Pair(true, R.string.solicitud_rechazada))
                                                                            }
                                                                        }
                                                                }

                                                            }
                                                    }
                                            }
                                        }
                                    }
                            }
                    }
                }
            return@withContext deferred.await()
        }
    }
    /** Esta funcion edita el atributo rutVinculado a "" en los 2actores de esta solicitud **/
    override suspend fun desvincularUsuarios(): Pair<Boolean, Int> = withContext(ioDispatcher) {
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean, Int>>()
            cloudDB.collection("Usuarios")
                .whereEqualTo("rut", usuarioDao.obtenerUsuarios()[0].rut)
                .get()
                .addOnFailureListener{
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Pair(false, R.string.error_cloud_request))
                    }else{
                        val aux = it.documents[0].data as MutableMap<String, Any>
                        val rutVinculado = aux["rutVinculado"] as String
                        aux["rutVinculado"] = ""
                        cloudDB.collection("Usuarios")
                            .document(it.documents[0].id)
                            .update(aux)
                            .addOnFailureListener{
                                deferred.complete(Pair(false, R.string.error_cloud_request))
                            }
                            .addOnSuccessListener{
                                cloudDB.collection("Usuarios")
                                    .whereEqualTo("rut", rutVinculado)
                                    .get()
                                    .addOnFailureListener{
                                        deferred.complete(Pair(false, R.string.error_cloud_request))
                                    }
                                    .addOnSuccessListener{
                                        if(it.isEmpty){
                                            deferred.complete(Pair(false, R.string.error_cloud_request))
                                        }else{
                                            val aux2 = it.documents[0].data as MutableMap<String, Any>
                                            aux2["rutVinculado"] = ""
                                            cloudDB.collection("Usuarios")
                                                .document(it.documents[0].id)
                                                .update(aux2)
                                                .addOnFailureListener{
                                                    deferred.complete(Pair(false, R.string.error_cloud_request))
                                                }
                                                .addOnSuccessListener{
                                                    deferred.complete(Pair(true, R.string.exito_cuenta_desvinculada))
                                                }
                                        }
                                    }
                            }
                    }
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun cargandoListaDeContactosDeEmergencia():
            Triple<Boolean, Int, MutableList<ContactoDeEmergencia>> = withContext(ioDispatcher) {
        withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Triple<
                    Boolean, Int, MutableList<ContactoDeEmergencia>>>()
            val usuario= usuarioDao.obtenerUsuarios()[0]

            if(usuario.perfil == "Dueño de casa"){
                cloudDB.collection("ContactoDeAsistencia")
                    .whereEqualTo("rut", usuario.rut)
                    .get()
                    .addOnFailureListener{
                        deferred.complete(Triple(false, R.string.error_cloud_request, defaultContactosDeEmergencia))
                    }
                    .addOnSuccessListener{
                        if(it.isEmpty){
                            deferred.complete(Triple(true, R.string.exito, defaultContactosDeEmergencia))
                            Log.e("cargandoListaDeContactosDeEmergencia", "cuenta no vinculada")
                        }else{
                            it.documents.forEachIndexed{ index, documentSnapshot ->
                                defaultContactosDeEmergencia.add(0,ContactoDeEmergencia(
                                    "$index"+"$index",
                                    documentSnapshot.getString("nombre").toString(),
                                    documentSnapshot.getString("telefono").toString(),
                                    fotoDuenoDeCasa,null
                                ))
                            }
                            deferred.complete(Triple(true, R.string.exito, defaultContactosDeEmergencia))
                        }
                    }
            }else{
                cloudDB.collection("Usuarios")
                    .whereEqualTo("rut", usuario.rut)
                    .get()
                    .addOnFailureListener {
                        deferred.complete(Triple(false, R.string.error_cloud_request, defaultContactosDeEmergencia))
                    }
                    .addOnSuccessListener{
                        if(it.isEmpty){
                            deferred.complete(Triple(true, R.string.exito, defaultContactosDeEmergencia))
                            Log.e("cargandoListaDeContactosDeEmergencia", "cuenta no vinculada")
                        }else{
                            cloudDB.collection("ContactoDeAsistencia")
                                .whereEqualTo("rut", it.documents[0].getString("rutVinculado"))
                                .get()
                                .addOnFailureListener{
                                    deferred.complete(Triple(false, R.string.error_cloud_request, defaultContactosDeEmergencia))
                                }.addOnSuccessListener {
                                    if(it.isEmpty){
                                        deferred.complete(Triple(true, R.string.exito, defaultContactosDeEmergencia))
                                        Log.e("cargandoListaDeContactosDeEmergencia", "cuenta no vinculada")
                                    }else{
                                        it.documents.forEachIndexed{ index, documentSnapshot ->
                                            defaultContactosDeEmergencia.add(0,ContactoDeEmergencia(
                                                "$index"+"$index",
                                                documentSnapshot.getString("nombre").toString(),
                                                documentSnapshot.getString("telefono").toString(),
                                                fotoDuenoDeCasa,
                                                documentSnapshot.getString("rut").toString()
                                            ))
                                        }
                                        deferred.complete(Triple(true, R.string.exito, defaultContactosDeEmergencia))
                                    }
                                }

                        }
                    }
            }
            return@withContext deferred.await()
        }
    }
    override suspend fun crearContactoDeAsistencia(nombre: String, telefono: String): Pair<Boolean, Int> = withContext(ioDispatcher) {
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean, Int>>()
            val contactoDeAsistenciaMap = ContactoDeAsistencia(
                usuarioDao.obtenerUsuarios()[0].rut,
                nombre,
                telefono
            ).toMap()

            cloudDB.collection("ContactoDeAsistencia")
                .add(contactoDeAsistenciaMap)
                .addOnFailureListener {
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener {
                    deferred.complete(Pair(true, R.string.exito))
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun recuperarClave(rut: String): Pair<Boolean, String> = withContext(ioDispatcher) {
        val deferred = CompletableDeferred<Pair<Boolean, String>>()
        try {
            val response = RecuperarClaveApi.RETROFIT_RECUPERAR_CLAVE.recuperarClave(rut).execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    deferred.complete(Pair(true, apiResponse.msg))
                } else {
                    deferred.complete(Pair(false, "Null response body"))
                }
            } else if (response.code() == 404) {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseErrorResponse(errorBody)
                deferred.complete(Pair(false, errorMessage))
            } else {
                deferred.complete(Pair(false, "Unknown error"))
            }
        } catch (e: Exception) {
            deferred.complete(Pair(false, e.message.toString()))
        }
        return@withContext deferred.await()
    }
    override suspend fun enviarSugerencia(comentario: String): Pair<Boolean, Int> = withContext(ioDispatcher) {
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean, Int>>()
            val sugerencia = mapOf("comentario" to comentario, "rut" to usuarioDao.obtenerUsuarios()[0].rut)
            cloudDB.collection("Sugerencias").add(sugerencia)
                .addOnFailureListener {
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener {
                    deferred.complete(Pair(true, R.string.exito))
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun actualizarDatosDelUsuario(nombreCompleto: String,
                                                   telefono: String):
            Pair<Boolean, Int> = withContext(ioDispatcher) {
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean, Int>>()
            val user = usuarioDao.obtenerUsuarios()[0]
            val map = mapOf("nombreCompleto" to nombreCompleto, "telefono" to telefono)
            cloudDB.collection("Usuarios")
                    .whereEqualTo("rut", user.rut)
                    .get()
                    .addOnFailureListener {
                        Log.e("actualizarDatosDelUsuario", "error al obtener usuario desde cloud")
                        deferred.complete(Pair(false, R.string.error_cloud_request))
                    }
                    .addOnSuccessListener {
                        if(it.isEmpty){
                            Log.e("actualizarDatosDelUsuario", "error al obtener usuario desde cloud")
                            deferred.complete(Pair(false, R.string.error_cloud_request))
                        }else{
                            cloudDB.collection("Usuarios")
                                .document(it.documents[0].id)
                                .update(map)
                                    .addOnFailureListener {
                                        Log.e("actualizarDatosDelUsuario", "error al actualizar el usuario en firestore")
                                        deferred.complete(Pair(false, R.string.error_cloud_request))
                                    }
                                    .addOnSuccessListener {
                                        deferred.complete(Pair(true, R.string.datos_actualizados))
                                    }
                        }
                    }
            return@withContext deferred.await()
        }
    }
    override suspend fun actualizarPassword(password: String)
    : Pair<Boolean, Int> = withContext(ioDispatcher) {
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean,Int>>()
            val currentUser = firebaseAuth.currentUser
            val user = usuarioDao.obtenerUsuarios()[0]
            val credential = EmailAuthProvider.getCredential(currentUser!!.email!!, user.password)
            currentUser.reauthenticate(credential)
                .addOnFailureListener {
                    deferred.complete(Pair(false, R.string.error_al_cambiar_password_en_firestore))
                }
                .addOnSuccessListener {
                    currentUser.updatePassword(password)
                        .addOnFailureListener {
                            deferred.complete(Pair(false, R.string.error_al_cambiar_password_en_firestore))
                        }
                        .addOnSuccessListener {
                            deferred.complete(Pair(true, R.string.exito))
                        }
                }
            return@withContext deferred.await()
        }
    }

    override suspend fun obtenerUsuarioVinculado(): Triple<Boolean, Int, DataUsuarioEnFirestore?> = withContext(ioDispatcher) {
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Triple<Boolean,Int, DataUsuarioEnFirestore?>>()
            cloudDB.collection("Usuarios")
                .whereEqualTo("rut", usuarioDao.obtenerUsuarios()[0].rut)
                .get()
                .addOnFailureListener{
                    deferred.complete(
                        Triple(
                            false,
                            R.string.error_cloud_request,
                            null
                        )
                    )
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(
                            Triple(
                                false,
                                R.string.error_cloud_request,
                                null
                            )
                        )
                    }else{
                        deferred.complete(
                            Triple(
                                true,
                                R.string.exito,
                                it.documents[0].toObject(DataUsuarioEnFirestore::class.java)
                            )
                        )
                    }
                }
            return@withContext deferred.await()
        }
    }


    override suspend fun registrarLlamadoDeEmergencia(llamadoDeEmergencia: LlamadoDeEmergencia):
            Pair<Boolean, Int> = withContext(ioDispatcher) {
        withContext(ioDispatcher) {
            val deferred = CompletableDeferred<Pair<Boolean,Int>>()
            cloudDB.collection("LlamadosDeEmergencias")
                .add(llamadoDeEmergencia)
                .addOnFailureListener{
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener {
                    deferred.complete(Pair(true, R.string.exito))
                }
            return@withContext deferred.await()
        }
    }


    override suspend fun cargandoRegistroDeActividadAsesorDelHogar():
            Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?> = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?>>()

            cloudDB.collection("LlamadosDeEmergencias")
                .whereEqualTo("rut", usuarioDao.obtenerUsuarios()[0].rut)
                .get()
                .addOnFailureListener{
                    deferred.complete(Triple(false, R.string.error_cloud_request, null))
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Triple(true, R.string.error_cloud_request, mutableListOf()))
                    }else{
                        val llamadosDeEmergenciaList = it.documents.map { document ->
                            val data = document.data!!
                            val rut = data["rut"] as String
                            val fecha = data["fecha"] as String
                            val hora = data["hora"] as String
                            val direccion = data["direccion"] as String
                            val motivoDelLlamado = data["motivoDelLlamado"] as String
                            val hogarDeLaEmergencia = data["hogarDeLaEmergencia"] as String
                            val id = document.id
                            LlamadoDeEmergenciaEnRecyclerView(
                                rut,
                                fecha,
                                hora,
                                direccion,
                                motivoDelLlamado,
                                hogarDeLaEmergencia,
                                id
                            )
                        }
                        deferred.complete(Triple(true, R.string.exito, llamadosDeEmergenciaList.toMutableList()))
                    }
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun cargandoRegistroDeActividadDuenoDeCasa():
            Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?> = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?>>()
            cloudDB.collection("LlamadosDeEmergencias")
                .whereEqualTo("hogarDeLaEmergencia", usuarioDao.obtenerUsuarios()[0].rut)
                .get()
                .addOnFailureListener{
                    deferred.complete(Triple(false, R.string.error_cloud_request, null))
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Triple(true, R.string.error_cloud_request, mutableListOf()))
                    }else{
                        val llamadosDeEmergenciaList = it.documents.map { document ->
                            val data = document.data!!
                            val rut = data["rut"] as String
                            val fecha = data["fecha"] as String
                            val hora = data["hora"] as String
                            val direccion = data["direccion"] as String
                            val motivoDelLlamado = data["motivoDelLlamado"] as String
                            val hogarDeLaEmergencia = data["hogarDeLaEmergencia"] as String
                            val id = document.id
                            LlamadoDeEmergenciaEnRecyclerView(
                                rut,
                                fecha,
                                hora,
                                direccion,
                                motivoDelLlamado,
                                hogarDeLaEmergencia,
                                id
                            )
                        }
                        deferred.complete(Triple(true, R.string.exito, llamadosDeEmergenciaList.toMutableList()))
                    }
                }
            return@withContext deferred.await()
        }
    }

    override suspend fun registrarLocalizacionEnEmergencia(geoPoint: GeoPoint){
        val rut = usuarioDao.obtenerUsuarios()[0].rut
        withContext(ioDispatcher){
            cloudDB.collection("registroLocalizacionEnEmergencia")
                .document(rut)
                .get()
                .addOnSuccessListener{
                    if(it.exists()){
                        val newListOfGeoPoints = it.data!!["geoPoints"] as MutableList<GeoPoint>
                        newListOfGeoPoints.add(geoPoint)
                        cloudDB.collection("registroLocalizacionEnEmergencia")
                            .document(rut)
                            .update("geoPoints", newListOfGeoPoints)
                    }else{
                        val listOfGeoPoints = mutableListOf<GeoPoint>()
                        listOfGeoPoints.add(geoPoint)
                        cloudDB.collection("registroLocalizacionEnEmergencia")
                            .document(rut)
                            .set(mapOf("geoPoints" to listOfGeoPoints))
                    }
                }
                .addOnFailureListener{
                    Log.e("ERROR", "Error al registrar la localizacion en emergencia")
                }
        }
    }

    override suspend fun guardarRutVinculado(rutVinculadoDBO: RutVinculadoDBO) {
        CoroutineScope(Dispatchers.IO).launch {
            rutVinculadoDao.guardarRutVinculado(rutVinculadoDBO)
        }
    }

    override suspend fun obtenerRutVinculado(): RutVinculadoDBO = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<RutVinculadoDBO>()
            deferred.complete(rutVinculadoDao.obtenerRutVinculados().first())
            return@withContext deferred.await()
        }
    }

    private fun parseErrorResponse(errorBody: String?): String {
        if (errorBody == null) return "Unknown error"
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<ApiResponse> = moshi.adapter(ApiResponse::class.java)
        val errorResponse: ApiResponse? = adapter.fromJson(errorBody)
        return errorResponse?.msg ?: "Unknown error"
    }


}

























