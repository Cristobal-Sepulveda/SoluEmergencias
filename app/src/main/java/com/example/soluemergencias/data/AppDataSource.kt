package com.example.soluemergencias.data

import android.content.Context
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.PreDataUsuarioEnFirestore

interface AppDataSource {
    /*suspend fun eliminarTokeanDeSesion()
    suspend fun validarTokenDeSesion(): Boolean*/
    suspend fun crearCuentaEnFirebaseAuthYFirestore(preDataUsuarioEnFirestore: PreDataUsuarioEnFirestore):Boolean
    suspend fun iniciarLoginYValidacionesConRut(rut:String): Boolean
    suspend fun obtenerUsuariosDesdeSqlite(): List<UsuarioDBO>
    suspend fun sesionActivaAFalseYLogout(context:Context):Boolean

}