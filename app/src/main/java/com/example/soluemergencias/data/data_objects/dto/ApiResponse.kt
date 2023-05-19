package com.example.soluemergencias.data.data_objects.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApiResponse(
    val msg: String,
): Parcelable {

}