package com.example.soluemergencias.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import com.example.soluemergencias.R

fun Activity.showAlertWithStringResources(title: Int, message: Int, function: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ ->
            function()
        }
        .show()
}