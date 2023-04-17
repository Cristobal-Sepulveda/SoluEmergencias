package com.example.soluemergencias.ui.login

import android.app.Application
import android.content.Context
import com.example.conductor.ui.base.BaseViewModel
import com.example.soluemergencias.data.AppDataSource
import com.example.soluemergencias.data.data_objects.domainObjects.Usuario

class LoginViewModel(val app: Application, val dataSource: AppDataSource): BaseViewModel(app) {

    suspend fun obtenerUsuariosDesdeFirestore(): MutableList<Usuario>{
        return dataSource.obtenerUsuariosDesdeFirestore()
    }

    suspend fun sesionActivaATrueYLogin(context: Context):Boolean{
        return dataSource.sesionActivaATrueYLogin(context)
    }


}