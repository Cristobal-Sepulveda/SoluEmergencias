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


    suspend fun iniciarLoginYValidacionesConRut(rut: String): Pair<Boolean, Int> {
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.iniciarLoginYValidacionesConRut(rut)
        _status.postValue(if(task.first) CloudRequestStatus.DONE else CloudRequestStatus.ERROR)
        return task
    }
}