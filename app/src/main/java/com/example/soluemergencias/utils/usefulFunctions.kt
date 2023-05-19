package com.example.soluemergencias.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
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

