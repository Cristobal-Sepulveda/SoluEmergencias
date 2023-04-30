package com.example.soluemergencias.utils

import android.util.Log
import android.view.View
import android.widget.ProgressBar
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
        else -> {
            progressBar.visibility = View.VISIBLE
        }
    }
}
