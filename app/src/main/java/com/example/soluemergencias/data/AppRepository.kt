package com.example.soluemergencias.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.soluemergencias.R
import com.example.soluemergencias.data.daos.UsuarioDao
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.utils.Constants.defaultContactosDeEmergencia
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.example.soluemergencias.utils.gettingLocalCurrentDateAndHour
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import java.util.*

@Suppress("LABEL_NAME_CLASH")
class AppRepository(private val context: Context,
                    private val usuarioDao: UsuarioDao,
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
                                                    documentoDeInteres["telefono"] as String,
                                                    documentoDeInteres["email"] as String,
                                                    documentoDeInteres["password"] as String,
                                                    documentoDeInteres["perfil"] as String,
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

    override suspend fun chequearSiHaySolicitudesPorAprobar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>> = withContext(ioDispatcher) {
        withContext(Dispatchers.IO){
            val rut = usuarioDao.obtenerUsuarios()[0].rut
            val deferred = CompletableDeferred<Triple<Boolean, Int,
                    MutableList<SolicitudDeVinculo>>>()

            cloudDB.collection("SolicitudesDeVinculacion")
                .whereEqualTo("rutDelReceptor", rut)
                .whereEqualTo("solicitudGestionada", false)
                .get()
                .addOnCompleteListener{
                    deferred.complete(
                        when(it.isSuccessful){
                            false -> Triple(false, R.string.error_cloud_request, mutableListOf())
                            true -> {
                                when(it.result.isEmpty){
                                    false -> Triple(true, R.string.exito, it.result.toObjects(SolicitudDeVinculo::class.java))
                                    true -> Triple(true, R.string.no_hay_solicitudes_por_aprobar, mutableListOf())
                                }
                            }
                        }
                    )
                }
            deferred.await()
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

    override suspend fun aprobarORechazarSolicitudDeVinculo(rutSolicitante: String, boolean:Boolean):
            Pair<Boolean, Int> = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Pair<Boolean,Int>>()
            cloudDB.collection("SolicitudesDeVinculacion")
                .whereEqualTo("rutDelSolicitante", rutSolicitante)
                .whereEqualTo("rutDelReceptor", usuarioDao.obtenerUsuarios()[0].rut)
                .get()
                .addOnFailureListener{
                    deferred.complete(Pair(false, R.string.error_cloud_request))
                }
                .addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Pair(false, R.string.error_cloud_request))
                    }else{
                        cloudDB.collection("SolicitudesDeVinculacion")
                            .document(it.documents[0].id)
                            .update("solicitudAprobada", boolean, "solicitudGestionada", true)
                            .addOnFailureListener{ deferred.complete(Pair(false, R.string.error_cloud_request)) }
                            .addOnSuccessListener {
                                when(boolean){
                                    true -> deferred.complete(Pair(true, R.string.solicitud_aprobada))
                                    false -> deferred.complete(Pair(true, R.string.solicitud_rechazada))
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
            cloudDB.collection("SolicitudesDeVinculacion")
                .whereEqualTo("rutDelReceptor", usuarioDao.obtenerUsuarios()[0].rut)
                .whereEqualTo("solicitudAprobada", true)
                .whereEqualTo("solicitudGestionada", true)
                .get()
                .addOnFailureListener {
                    deferred.complete(Triple(false, R.string.error_cloud_request, defaultContactosDeEmergencia))
                }.addOnSuccessListener{
                    if(it.isEmpty){
                        deferred.complete(Triple(true, R.string.exito, defaultContactosDeEmergencia))
                        Log.e("cargandoListaDeContactosDeEmergencia", "cuenta no vinculada")
                    }else{
                        Log.e("cargandoListaDeContactosDeEmergencia", "cuenta vinculada")
                        deferred.complete(Triple(true, R.string.exito, defaultContactosDeEmergencia))
                    }
                }
            return@withContext deferred.await()
        }
    }
}
