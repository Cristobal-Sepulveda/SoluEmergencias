package com.example.soluemergencias.utils

import android.app.Activity
import android.content.Context
import com.google.android.material.snackbar.Snackbar


fun Activity.mostrarSnackBarEnMainThread(context: Context, mensaje: Int, duracion: Int = Snackbar.LENGTH_LONG){
    Snackbar.make(findViewById(android.R.id.content), mensaje, duracion).show()
}