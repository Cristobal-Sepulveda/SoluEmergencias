package com.example.soluemergencias.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast


fun mostrarToastEnMainThread(context: Context, message: Int){
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

