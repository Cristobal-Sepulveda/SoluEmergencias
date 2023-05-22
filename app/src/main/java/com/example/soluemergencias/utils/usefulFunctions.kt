package com.example.soluemergencias.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.widget.Toast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


fun showToastInMainThreadWithStringResource(context: Context, message: Int){
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

fun showToastInMainThreadWithHardcoreString(context: Context, message: String){
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

fun gettingLocalCurrentDateAndHour(): Pair<String, String> {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale("es", "CL"))
    val now = LocalDateTime.now(TimeZone.getTimeZone("America/Santiago").toZoneId())
    val dateAndHour = now.format(formatter)
    val aux = dateAndHour.split(" ")
    val date = aux[0]
    val hour = aux[1]
    return Pair(date, hour)
}

fun isThisRutValid(rutAValidar: String): Boolean{
    val rutAux = if(rutAValidar.length == 8) rutAValidar.padStart(9, '0') else rutAValidar
    var rutClean = rutAux.replace(".", "").replace("-", "")
    if (rutClean.length != 9) return false
    var dv = rutClean.last().toUpperCase()
    if (!dv.isDigit() && dv != 'K') return false
    rutClean = rutClean.dropLast(1)
    var sum = 0
    var factor = 2
    for (i in rutClean.reversed()) {
        sum += (i.toString().toInt() * factor)
        factor = if (factor == 7) 2 else factor + 1
    }
    val dvCalc = 11 - (sum % 11)
    val dvExpected = when (dvCalc) {
        11 -> "0"
        10 -> "K"
        else -> dvCalc.toString()
    }
    return dv.toString() == dvExpected
}

fun parsingBase64ImageToBitMap(fotoPerfil: String): Bitmap {
    return if (fotoPerfil.last().toString() == "=" ||
        (fotoPerfil.first().toString() == "/" && fotoPerfil[1].toString() == "9")
    ) {
        val decodedString = Base64.decode(fotoPerfil, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } else {
        val aux2 = fotoPerfil.indexOf("=") + 1
        val aux3 = fotoPerfil.substring(0, aux2)
        val decodedString = Base64.decode(aux3, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}



