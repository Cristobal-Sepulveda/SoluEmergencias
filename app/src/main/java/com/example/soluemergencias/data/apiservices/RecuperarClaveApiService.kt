package com.example.soluemergencias.data.apiservices

import com.example.soluemergencias.data.apiservices.MoshiProvider.moshiConverterFactory
import com.example.soluemergencias.data.data_objects.dto.ApiResponse
import com.example.soluemergencias.utils.Constants.backend_url
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface RecuperarClaveApiService {

    @POST("recuperarClave")
    fun recuperarClave(@Header("rut") rut: String): Call<ApiResponse>

}

object RecuperarClaveApi{
    private val retrofitRecuperarClave = Retrofit.Builder()
        .addConverterFactory(moshiConverterFactory)
        .baseUrl(backend_url)
        .build()

    val RETROFIT_RECUPERAR_CLAVE: RecuperarClaveApiService by lazy{
        retrofitRecuperarClave.create(RecuperarClaveApiService::class.java)
    }
}
