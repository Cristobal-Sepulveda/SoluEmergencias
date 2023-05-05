package com.example.soluemergencias.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.soluemergencias.R
import com.example.soluemergencias.data.daos.UsuarioDao
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.PreDataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.example.soluemergencias.utils.gettingLocalCurrentDateAndHour
import com.example.soluemergencias.utils.showToastInMainThreadWithStringResource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("LABEL_NAME_CLASH")
class AppRepository(private val context: Context,
                    private val usuarioDao: UsuarioDao,
                    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): AppDataSource {

    private val cloudDB = FirebaseFirestore.getInstance()

    override suspend fun crearCuentaEnFirebaseAuthYFirestore(preDataUsuarioEnFirestore: PreDataUsuarioEnFirestore): Boolean = withContext(ioDispatcher) {
        val deferred = CompletableDeferred<Boolean>()
        val fotoPerfil = preDataUsuarioEnFirestore.fotoPerfil
        val nombreCompleto = preDataUsuarioEnFirestore.nombreCompleto
        val rut = preDataUsuarioEnFirestore.rut
        val telefono = preDataUsuarioEnFirestore.telefono
        val email = preDataUsuarioEnFirestore.email
        val password = preDataUsuarioEnFirestore.password
        val perfil = preDataUsuarioEnFirestore.perfil
        cloudDB.collection("Usuarios").document(rut).get()
            .addOnFailureListener{
                Log.e("Error", "Error al crear usuario en firestore: ${it.message}")
                deferred.complete(false)
            }.addOnSuccessListener {
                if(it.exists()){
                    showToastInMainThreadWithStringResource(context, R.string.rut_ya_esta_en_uso)
                    deferred.complete(true)
                }else{
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnFailureListener{
                            Log.e("Error", "Error al crear usuario en firebaseAuth : ${it.message}")
                            if(it.message!!.contains("email address is already in use")){
                                showToastInMainThreadWithStringResource(context, R.string.email_en_uso)
                            }else{
                                showToastInMainThreadWithStringResource(context, R.string.error_al_crear_usuario_en_firebase_auth)
                            }
                            deferred.complete(false)
                        }.addOnSuccessListener {
                            firebaseAuth.signOut()
                            val nuevoUsuario = DataUsuarioEnFirestore(fotoPerfil,
                                nombreCompleto,
                                rut,
                                telefono,
                                email,
                                password,
                                perfil
                            )
                            cloudDB.collection("Usuarios")
                                .document(preDataUsuarioEnFirestore.rut).set(nuevoUsuario)
                                .addOnFailureListener {
                                    Log.e("Error", "Error al crear usuario en firestore: ${it.message}")
                                    deferred.complete(false)
                                }
                                .addOnSuccessListener {
                                    showToastInMainThreadWithStringResource(context, R.string.usuario_creado_con_exito)
                                    deferred.complete(true)
                                }
                        }
                }
            }
        return@withContext deferred.await()
    }
    override suspend fun iniciarLoginYValidacionesConRut(rut: String): Boolean = withContext(ioDispatcher) {
        val deferred = CompletableDeferred<Boolean>()
        cloudDB.collection("Usuarios").get()
            .addOnFailureListener { exception ->
                showToastInMainThreadWithStringResource(context, R.string.error_al_obtener_usuarios_en_firestore)
                deferred.complete(false)
            }
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach { document ->
                    if (document.get("rut") as String == rut) {
                        if (document.get("sesionActiva") as Boolean) {
                            showToastInMainThreadWithStringResource(context, R.string.usuario_ya_tiene_sesion_activa)
                            deferred.complete(false)
                        } else {
                            firebaseAuth.signInWithEmailAndPassword(document.get("email") as String, document.get("password") as String)
                                .addOnFailureListener { exception ->
                                    Log.e("Error firebaseAuth", "Error al iniciar sesion en firebaseAuth: ${exception.message}")
                                    showToastInMainThreadWithStringResource(context, R.string.error_al_iniciar_sesion_en_firebase_auth)
                                    deferred.complete(false)
                                }
                                .addOnSuccessListener {
                                    cloudDB.collection("Usuarios")
                                        .document(document.get("rut") as String)
                                        .update("sesionActiva", true)
                                        .addOnFailureListener { exception ->
                                            deferred.complete(false)
                                        }
                                        .addOnSuccessListener {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                usuarioDao.guardarUsuario(UsuarioDBO(
                                                    document.get("fotoPerfil") as String,
                                                    document.get("nombreCompleto") as String,
                                                    document.get("rut") as String,
                                                    document.get("telefono") as String,
                                                    document.get("email") as String,
                                                    document.get("password") as String,
                                                    document.get("perfil") as String,
                                                ))
                                            }
                                            deferred.complete(true)
                                        }
                                }
                        }
                    }
                }
            }
        return@withContext deferred.await()
    }
    override suspend fun sesionActivaAFalseYLogout(context: Context):Boolean = withContext(ioDispatcher){
        withContext(ioDispatcher) {
            val deferred = CompletableDeferred<Boolean>()

            cloudDB.collection("Usuarios")
                .document(usuarioDao.obtenerUsuarios()[0].rut)
                .update("sesionActiva", false)
                .addOnFailureListener() {
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                    deferred.complete(false)
                }
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch{
                        usuarioDao.eliminarUsuarios()
                    }
                    deferred.complete(true)
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
                .whereEqualTo("solicitudAprobada", false)
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
            val (_,nombreCompleto, rut) = usuarioDao.obtenerUsuarios()[0]
            val (date, hour) = gettingLocalCurrentDateAndHour()
            val solicitudDeVinculoMap = SolicitudDeVinculo(rut, nombreCompleto,
                rutAVincular, date, hour, false).toMap()

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
            return@withContext deferred.await()
        }
    }
}
