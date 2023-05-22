package com.example.soluemergencias.ui.vincularcuentas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class VincularCuentasViewModel(val dataSource: AppDataSource): ViewModel() {
    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status

    private val _statusEnviadas = MutableLiveData<CloudRequestStatus>()
    val statusEnviadas: MutableLiveData<CloudRequestStatus>
        get() = _statusEnviadas

    private val _solicitudesRecibidasInScreen = MutableLiveData<MutableList<SolicitudDeVinculo>?>()
    val solicitudesRecibidasInScreen: MutableLiveData<MutableList<SolicitudDeVinculo>?>
        get() = _solicitudesRecibidasInScreen

    private val _solicitudesEnviadasInScreen = MutableLiveData<MutableList<SolicitudDeVinculo>?>()
    val solicitudesEnviadasInScreen: MutableLiveData<MutableList<SolicitudDeVinculo>?>
        get() = _solicitudesEnviadasInScreen

    suspend fun chequearSiHaySolicitudesDeVinculacionRecibidasSinGestionar(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>{
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.chequearSiHaySolicitudesDeVinculacionRecibidasSinGestionar()
        _status.postValue(
            when {
                task.third.isEmpty() && !task.first -> CloudRequestStatus.ERROR
                task.third.isEmpty() && task.first -> CloudRequestStatus.DONE_WITH_NO_DATA
                else -> {
                    _solicitudesRecibidasInScreen.postValue(task.third)
                    CloudRequestStatus.DONE
                }
            }
        )
        return task
    }

    suspend fun chequearSiHaySolicitudesDeVinculacionEnviadas(): Triple<Boolean, Int, MutableList<SolicitudDeVinculo>>{
        _statusEnviadas.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.chequearSiHaySolicitudesDeVinculacionEnviadas()
        _statusEnviadas.postValue(
            when {
                task.third.isEmpty() && !task.first -> CloudRequestStatus.ERROR
                task.third.isEmpty() && task.first -> CloudRequestStatus.DONE_WITH_NO_DATA
                else -> {
                    _solicitudesEnviadasInScreen.postValue(task.third)
                    CloudRequestStatus.DONE
                }
            }
        )
        return task
    }

    suspend fun crearSolicitudDeVinculo(rutAVincular: String): Pair<Boolean, Int>{
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.crearSolicitudDeVinculo(rutAVincular)
        _status.postValue(if(task.first) CloudRequestStatus.DONE else CloudRequestStatus.ERROR)
        return task
    }

    fun vaciarRecyclerView(){
        _solicitudesRecibidasInScreen.value = mutableListOf()
    }
}