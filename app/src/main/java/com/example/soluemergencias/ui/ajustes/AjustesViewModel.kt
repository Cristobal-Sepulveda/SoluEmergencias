package com.example.soluemergencias.ui.ajustes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class AjustesViewModel(val dataSource: AppDataSource): ViewModel() {

    private val _status: MutableLiveData<CloudRequestStatus> = MutableLiveData()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status

    suspend fun obtenerDatosDelUsuario(): UsuarioDBO{
        return dataSource.obtenerUsuarioDesdeRoom()
    }
    suspend fun actualizarDatosDelUsuario(nombreCompleto: String, telefono: String): Pair<Boolean,Int>{
        _status.postValue(CloudRequestStatus.LOADING)
        val response = dataSource.actualizarDatosDelUsuario(nombreCompleto, telefono)
        _status.postValue(when(response.first){
            true -> CloudRequestStatus.DONE
            false -> CloudRequestStatus.ERROR
        })
        return response
    }
    suspend fun actualizarPassword(password: String): Pair<Boolean,Int>{
        _status.postValue(CloudRequestStatus.LOADING)
        val response = dataSource.actualizarPassword(password)
        _status.postValue(when(response.first){
            true -> CloudRequestStatus.DONE
            false -> CloudRequestStatus.ERROR
        })
        return response
    }
}