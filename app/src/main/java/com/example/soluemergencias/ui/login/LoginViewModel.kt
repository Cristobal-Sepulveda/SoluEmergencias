package com.example.soluemergencias.ui.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conductor.ui.base.BaseViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class LoginViewModel(val app: Application, val dataSource: AppDataSource): BaseViewModel(app) {

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