package com.example.soluemergencias.ui.vistageneral

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.dbo.UsuarioDBO
import com.example.soluemergencias.data.data_objects.domainObjects.ContactoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.DataUsuarioEnFirestore
import com.example.soluemergencias.data.data_objects.domainObjects.LlamadoDeEmergencia
import com.example.soluemergencias.data.data_objects.domainObjects.SolicitudDeVinculo
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class VistaGeneralViewModel(private val dataSource: AppDataSource,) : ViewModel(){
    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status

    private val _contactosDeEmergenciaInScreen = MutableLiveData<MutableList<ContactoDeEmergencia>?>()
    val contactosDeEmergenciaInScreen: MutableLiveData<MutableList<ContactoDeEmergencia>?>
        get() = _contactosDeEmergenciaInScreen

    suspend fun obtenerUsuarioDesdeRoom(): UsuarioDBO {
        return dataSource.obtenerUsuarioDesdeRoom()
    }
    suspend fun registrarLlamadoDeEmergencia(llamadoDeEmergencia: LlamadoDeEmergencia){
        dataSource.registrarLlamadoDeEmergencia(llamadoDeEmergencia)
    }
    suspend fun cargandoListaDeContactosDeEmergencia():
            Triple<Boolean, Int, MutableList<ContactoDeEmergencia>>{

        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.cargandoListaDeContactosDeEmergencia()
        _status.postValue(when(task.first){
            true -> CloudRequestStatus.DONE
            false -> CloudRequestStatus.ERROR
        })
        _contactosDeEmergenciaInScreen.postValue(task.third)
        return task
    }

    suspend fun obtenerUsuarioVinculado(): Triple<Boolean, Int, DataUsuarioEnFirestore?>{
        return dataSource.obtenerUsuarioVinculado()
    }

    suspend fun desvincularUsuarios(): Pair<Boolean, Int>{
        _status.postValue(CloudRequestStatus.LOADING)
        val task = dataSource.desvincularUsuarios()
        _status.postValue(when(task.first){
            true -> CloudRequestStatus.DONE
            false -> CloudRequestStatus.ERROR
        })
        return task
    }
}