package com.example.soluemergencias.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.utils.Constants.CloudRequestStatus

class MapViewModel(val dataSource: AppDataSource): ViewModel() {

    private val _status = MutableLiveData<CloudRequestStatus>()
    val status: MutableLiveData<CloudRequestStatus>
        get() = _status



}