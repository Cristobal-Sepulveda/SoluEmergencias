package com.example.soluemergencias.data

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.soluemergencias.AuthenticationActivity
import com.example.soluemergencias.MainActivity
import com.example.soluemergencias.R
import com.example.soluemergencias.data.daos.UsuarioDao
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.Usuario
import com.example.soluemergencias.utils.Constants.firebaseAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

@Suppress("LABEL_NAME_CLASH")
class AppRepository(private val usuarioDao: UsuarioDao,
                    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): AppDataSource {

    private val cloudDB = FirebaseFirestore.getInstance()

    override suspend fun ingresarUsuarioAFirestore(usuario: Usuario, context: Context): Boolean = withContext(ioDispatcher) {
        withContext(ioDispatcher) {
            val deferred = CompletableDeferred<Boolean>()

            cloudDB.collection("Usuarios")
                .document(usuario.id).set(usuario)

                .addOnSuccessListener {
                    deferred.complete(true)
                }
                .addOnFailureListener {
                    Log.e("addOnFailureListener", "${it.message}")
                    Toast.makeText(context, R.string.error_al_crear_cuenta_fireauth, Toast.LENGTH_LONG).show()
                    deferred.complete(false)
                }
            return@withContext deferred.await()
        }
    }
    override suspend fun obtenerUsuariosDesdeFirestore(): MutableList<Usuario> = withContext(ioDispatcher) {
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<MutableList<Usuario>>()
            val listAux = mutableListOf<Usuario>()

            val colRef = cloudDB.collection("Usuarios").get()

            colRef.addOnSuccessListener{
                for (document in it){
                    val usuario = Usuario(
                        document.id,
                        document.get("nombreCompleto") as String,
                        document.get("fotoPerfil") as String,
                        document.get("telefono") as String,
                        document.get("usuario") as String,
                        document.get("password") as String,
                        document.get("sesionActiva") as Boolean,
                        document.get("perfil") as String
                    )
                    listAux.add(usuario)
                }
                deferred.complete(listAux)
            }
            colRef.addOnFailureListener{
                deferred.complete(listAux)
            }
            return@withContext deferred.await()
        }
    }
    override suspend fun obtenerUsuariosDesdeSqlite(): List<UsuarioDBO> = withContext(ioDispatcher) {
        withContext(ioDispatcher) {
                return@withContext usuarioDao.obtenerUsuarios()
        }
    }

    override suspend fun eliminarUsuariosEnSqlite() {
        withContext(ioDispatcher) {
            usuarioDao.eliminarUsuarios()
        }
    }

    override suspend fun sesionActivaATrueYLogin(context:Context): Boolean = withContext(ioDispatcher){
        withContext(ioDispatcher){
            val deferred = CompletableDeferred<Boolean>()
            cloudDB.collection("Usuarios")
                .document(firebaseAuth.currentUser!!.uid)
                .update("sesionActiva", true)
                .addOnSuccessListener {
                    deferred.complete(true)
                }
                .addOnFailureListener{
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                    deferred.complete(false)
                }
            deferred.await()
        }
    }

    override suspend fun sesionActivaAFalseYLogout(context: Context):Boolean = withContext(ioDispatcher){
        withContext(ioDispatcher) {
            val deferred = CompletableDeferred<Boolean>()
            cloudDB.collection("Usuarios")
                .document(firebaseAuth.currentUser!!.uid)
                .update("sesionActiva", false)
                .addOnFailureListener() {
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                    deferred.complete(false)
                }
                .addOnSuccessListener {
                    deferred.complete(true)
                }
            return@withContext deferred.await()
        }
    }
}
