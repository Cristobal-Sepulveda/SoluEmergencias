package com.example.soluemergencias.ui.crearcuenta

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conductor.ui.base.BaseViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.PreDataUsuarioEnFirestore
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class CrearCuentaViewModel(val app: Application, val dataSource: AppDataSource): BaseViewModel(app) {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: LiveData<CloudRequestStatus>
        get() = _status
    suspend fun crearCuentaEnFirebaseAuthYFirestore(preDataUsuarioEnFirestore: PreDataUsuarioEnFirestore) {
        _status.value = CloudRequestStatus.LOADING
        if(dataSource.crearCuentaEnFirebaseAuthYFirestore(preDataUsuarioEnFirestore)){
            _status.value = CloudRequestStatus.DONE
        }else{
            _status.value = CloudRequestStatus.ERROR
        }
    }
}