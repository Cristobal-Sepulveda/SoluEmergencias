package com.example.soluemergencias.ui.vistageneral

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants

class VistaGeneralViewModel(private val dataSource: AppDataSource,) : ViewModel(){
    private val _status = MutableLiveData<Constants.CloudRequestStatus>()
    val status: MutableLiveData<Constants.CloudRequestStatus>
        get() = _status
}