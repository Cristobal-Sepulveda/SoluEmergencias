package com.example.soluemergencias.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

fun Activity.mostrarSnackBarEnMainThread(context: Context, mensaje: Int, duracion: Int = Snackbar.LENGTH_LONG){
    Snackbar.make(findViewById(android.R.id.content), mensaje, duracion).show()
}

fun Activity.closeKeyboard(it: View){
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
}