package com.example.soluemergencias.ui.registro

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergenciaEnRecyclerView
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class RegistroViewModel(val dataSource: AppDataSource): ViewModel() {
    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status

    private val _llamadosDeEmergencia = MutableLiveData<MutableList<LlamadoDeEmergenciaEnRecyclerView>>()
    val llamadosDeEmergencia: MutableLiveData<MutableList<LlamadoDeEmergenciaEnRecyclerView>>
        get() = _llamadosDeEmergencia


    suspend fun cargandoRegistroDeActividadAsesorDelHogar():
            Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?>{
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.cargandoRegistroDeActividadAsesorDelHogar()
        Log.e("TAG", task.third.toString())
        _status.postValue(when(task.first){
            true -> {
                if(task.third!!.isEmpty()){
                    CloudRequestStatus.DONE_WITH_NO_DATA
                }else{
                    _llamadosDeEmergencia.postValue(task.third!!)
                    CloudRequestStatus.DONE
                }
            }
            false -> CloudRequestStatus.ERROR
        })
        return task
    }

    suspend fun cargandoRegistroDeActividadDuenoDeCasa():
            Triple<Boolean, Int, MutableList<LlamadoDeEmergenciaEnRecyclerView>?>{
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.cargandoRegistroDeActividadDuenoDeCasa()
        _status.postValue(when(task.first){
            true -> {
                if(task.third!!.isEmpty()){
                    CloudRequestStatus.DONE_WITH_NO_DATA
                }else{
                    CloudRequestStatus.DONE
                }
            }
            false -> CloudRequestStatus.ERROR
        })
        return task
    }
}