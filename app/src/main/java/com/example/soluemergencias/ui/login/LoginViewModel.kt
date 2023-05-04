package com.example.soluemergencias.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class LoginViewModel(private val dataSource: AppDataSource): ViewModel() {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: LiveData<CloudRequestStatus>
        get() = _status



    suspend fun iniciarLoginYValidacionesConRut(rut: String): Boolean{
        _status.postValue(CloudRequestStatus.LOADING)
        return if(dataSource.iniciarLoginYValidacionesConRut(rut)){
            _status.postValue(CloudRequestStatus.DONE)
            true
        }else{
            _status.postValue(CloudRequestStatus.ERROR)
            false
        }
    }

}