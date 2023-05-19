package com.example.soluemergencias.ui.sugerencias

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class SugerenciasViewModel(private val dataSource: AppDataSource): ViewModel() {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status

    suspend fun enviarSugerencia(comentario: String): Pair<Boolean, Int>{
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.enviarSugerencia(comentario)
        _status.postValue(when(task.first){
            true -> CloudRequestStatus.DONE
            false -> CloudRequestStatus.ERROR
        })
        return task
    }
}