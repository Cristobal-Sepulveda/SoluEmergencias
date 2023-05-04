package com.example.soluemergencias.ui.crearcuenta

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.PreDataUsuarioEnFirestore
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class CrearCuentaViewModel(private val dataSource: AppDataSource): ViewModel() {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: LiveData<CloudRequestStatus>
        get() = _status



    suspend fun crearCuentaEnFirebaseAuthYFirestore(preDataUsuarioEnFirestore: PreDataUsuarioEnFirestore) {
        _status.value = CloudRequestStatus.LOADING
        _status.value = when (dataSource.crearCuentaEnFirebaseAuthYFirestore(preDataUsuarioEnFirestore)) {
                false -> CloudRequestStatus.ERROR
                true -> CloudRequestStatus.DONE
            }
    }
}