package com.example.soluemergencias.ui.crearcuenta

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class CrearCuentaViewModel(private val dataSource: AppDataSource): ViewModel() {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: LiveData<CloudRequestStatus>
        get() = _status


    suspend fun crearCuentaEnFirebaseAuthYFirestore(
        dataUsuarioEnFirestore: DataUsuarioEnFirestore
    ): Pair<Boolean,Int> {
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.crearCuentaEnFirebaseAuthYFirestore(dataUsuarioEnFirestore)
        _status.postValue(when(task.first) {
                false -> CloudRequestStatus.ERROR
                true -> CloudRequestStatus.DONE
            }
        )
        return task
    }
}