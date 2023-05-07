package com.example.soluemergencias.ui.vistageneral

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.utils.Constants

class VistaGeneralViewModel(private val dataSource: AppDataSource,) : ViewModel(){
    private val _status = MutableLiveData<Constants.CloudRequestStatus>()
    val status: MutableLiveData<Constants.CloudRequestStatus>
        get() = _status

    private val _contactosDeEmergenciaInScreen = MutableLiveData<MutableList<ContactoDeEmergencia>?>()
    val contactosDeEmergenciaInScreen: MutableLiveData<MutableList<ContactoDeEmergencia>?>
        get() = _contactosDeEmergenciaInScreen
    suspend fun cargandoListaDeContactosDeEmergencia():
            Triple<Boolean, Int, MutableList<ContactoDeEmergencia>>{

        _status.postValue(Constants.CloudRequestStatus.LOADING)
        val task = dataSource.cargandoListaDeContactosDeEmergencia()
        _status.postValue(when(task.first){
            true -> Constants.CloudRequestStatus.DONE
            false -> Constants.CloudRequestStatus.ERROR
        })
        _contactosDeEmergenciaInScreen.postValue(task.third)
        return task
    }
}