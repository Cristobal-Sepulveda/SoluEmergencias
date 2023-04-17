package com.example.soluemergencias.ui.crearcuenta

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.conductor.ui.base.BaseViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.Usuario
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearCuentaViewModel(val app: Application, val dataSource: AppDataSource): BaseViewModel(app) {

    suspend fun ingresarUsuarioAFirestore(usuario: Usuario, context: Context):Boolean{
        val deferred = CompletableDeferred<Boolean>()
        viewModelScope.launch{
            withContext(Dispatchers.IO){
                deferred.complete(dataSource.ingresarUsuarioAFirestore(usuario,context))
            }
        }
        return deferred.await()
    }

}