package com.example.soluemergencias.ui.recuperarclave

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class RecuperarClaveViewModel(private val dataSource: AppDataSource): ViewModel() {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: LiveData<CloudRequestStatus>
        get() = _status

    suspend fun recuperarClave(rut:String): Pair<Boolean, String>{
        _status.postValue(CloudRequestStatus.LOADING)
         val task = dataSource.recuperarClave(rut)
        _status.postValue(when(task.first){
            true -> CloudRequestStatus.DONE
            false -> CloudRequestStatus.ERROR
        })
        return task
    }

}