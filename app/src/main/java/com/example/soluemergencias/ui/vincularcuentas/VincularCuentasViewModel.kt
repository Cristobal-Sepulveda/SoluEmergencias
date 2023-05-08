package com.example.soluemergencias.ui.vincularcuentas

import androidx.lifecycle.MutableLiveData
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class VincularCuentasViewModel(val dataSource: AppDataSource) {
    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status

    private val _solicitudesInScreen = MutableLiveData<MutableList<SolicitudDeVinculo>?>()
    val solicitudesInScreen: MutableLiveData<MutableList<SolicitudDeVinculo>?>
        get() = _solicitudesInScreen

    suspend fun chequearSiHaySolicitudesPorAprobar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>{
        _status.postValue(CloudRequestStatus.LOADING)

        val task = dataSource.chequearSiHaySolicitudesPorAprobar()
/*        _status.postValue(
            when {
                task.third.isEmpty() && !task.first -> CloudRequestStatus.ERROR
                task.third.isEmpty() && task.first -> CloudRequestStatus.DONE_WITH_NO_DATA
                else -> {
                    _solicitudesInScreen.postValue(task.third)
                    CloudRequestStatus.DONE
                }
            }
        )*/
        return task
    }

    suspend fun crearSolicitudDeVinculo(rutAVincular: String): Pair<Boolean, Int>{
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.crearSolicitudDeVinculo(rutAVincular)
        _status.postValue(if(task.first) CloudRequestStatus.DONE else CloudRequestStatus.ERROR)
        return task
    }

    fun vaciarRecyclerView(){
        _solicitudesInScreen.value = mutableListOf()
    }
}