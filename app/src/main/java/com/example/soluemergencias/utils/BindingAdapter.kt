package com.example.soluemergencias.utils

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.example.soluemergencias.utils.Constants.CloudRequestStatus
@BindingAdapter("cloudRequestStatusCircularProgress")
fun bindStatusErrorCircularProgress(progressBar: ProgressBar, status: LiveData<CloudRequestStatus>?) {
    Log.d("BindingAdapter", "bindStatusErrorCircularProgress: ${status?.value}")
    when (status?.value) {
        CloudRequestStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        CloudRequestStatus.ERROR -> {
            progressBar.visibility = View.GONE
        }
        CloudRequestStatus.DONE -> {
            progressBar.visibility = View.GONE
        }
        CloudRequestStatus.DONE_WITH_NO_DATA -> {
            progressBar.visibility = View.GONE
        }
        else -> {
            progressBar.visibility = View.GONE
        }
    }
}

@BindingAdapter("cloudRequestStatusImage")
fun bindStatusErrorImage(imageView: ImageView, status: LiveData<CloudRequestStatus>?) {
    Log.d("BindingAdapter", "bindStatusErrorImage: ${status?.value}")
    when (status?.value) {
        CloudRequestStatus.LOADING -> {
            imageView.visibility = View.GONE
        }
        CloudRequestStatus.ERROR -> {
            imageView.visibility = View.VISIBLE
        }
        CloudRequestStatus.DONE -> {
            imageView.visibility = View.GONE
        }
        CloudRequestStatus.DONE_WITH_NO_DATA -> {
            imageView.visibility = View.GONE
        }
        else -> {
            imageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("cloudRequestStatusTextView")
fun bindStatusErrorTextView(textView: TextView, status: LiveData<CloudRequestStatus>?) {
    Log.d("BindingAdapter", "bindStatusErrorTextView: ${status?.value}")
    when (status?.value) {
        CloudRequestStatus.LOADING -> {
            textView.visibility = View.GONE
        }
        CloudRequestStatus.ERROR -> {
            textView.visibility = View.GONE
        }
        CloudRequestStatus.DONE -> {
            textView.visibility = View.GONE
        }
        CloudRequestStatus.DONE_WITH_NO_DATA -> {
            textView.visibility = View.VISIBLE
        }
        else -> {
            textView.visibility = View.GONE
        }
    }
}

@BindingAdapter("cambiarBooleanAString")
fun bindCambiarBooleanAString(textView: TextView, boolean: Boolean){
    when(boolean){
        true -> {
            textView.text = "Si"
        }
        false -> {
            textView.text = "No"
        }
    }
}
@BindingAdapter("boolean1", "boolean2")
fun bindCambiarBooleanAString2(textView: TextView, boolean1: Boolean, boolean2: Boolean){
    if(boolean1){
        if(boolean2) {
            textView.text = "Aprobada"
        }else{
            textView.text = "Rechazada"
        }
    }else{
        textView.text = "Sin gestionar"
    }
}

@BindingAdapter("seteandoRegistroItem", "atributo")
fun bindSeteandoRegistroItem(textView: TextView, text: String, atributo: String){
    when(atributo){
        "fecha" -> textView.text = "Fecha: $text"
        "hora" -> textView.text = "Hora: $text"
        "geopoint" -> textView.text = "Dirección = $text"
        "detalles" -> textView.text = "Detalles: $text"
    }
}
