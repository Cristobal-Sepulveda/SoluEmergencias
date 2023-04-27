package com.example.soluemergencias.data

import android.content.Context
import com.example.soluemergencias.MainActivity
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.Usuario

interface AppDataSource {
    /*suspend fun eliminarTokeanDeSesion()
    suspend fun validarTokenDeSesion(): Boolean*/
    suspend fun ingresarUsuarioAFirestore(usuario: Usuario, context: Context):Boolean

    suspend fun obtenerUsuariosDesdeFirestore(): MutableList<Usuario>
    suspend fun obtenerUsuariosDesdeSqlite(): List<UsuarioDBO>
    suspend fun eliminarUsuariosEnSqlite()
    suspend fun sesionActivaATrueYLogin(context:Context): Boolean
    suspend fun sesionActivaAFalseYLogout(context:Context):Boolean

}