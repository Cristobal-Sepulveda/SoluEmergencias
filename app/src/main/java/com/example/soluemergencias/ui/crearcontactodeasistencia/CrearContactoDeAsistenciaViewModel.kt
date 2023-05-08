package com.example.soluemergencias.ui.crearcontactodeasistencia

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class CrearContactoDeAsistenciaViewModel(val dataSource: AppDataSource): ViewModel() {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status

    suspend fun crearContactoDeAsistencia(nombre: String, telefono: String): Pair<Boolean, Int> {
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.crearContactoDeAsistencia(nombre, telefono)
        _status.postValue(when(task.first){
            true -> CloudRequestStatus.DONE
            false -> CloudRequestStatus.ERROR
        })
        return task
    }
}