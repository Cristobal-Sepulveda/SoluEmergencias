package com.example.soluemergencias.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import com.example.soluemergencias.R

fun Activity.withPermissions(vararg permissions: String, callback: () -> Unit) {
    val checkingPermissions = permissions.filter {
        checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
    }
    if (checkingPermissions.isEmpty()) {
        // All permissions are granted, execute callback
        callback()
    } else {
        // Request permissions
        requestPermissions(checkingPermissions.toTypedArray(), 0)
    }
}

fun Activity.showAlert(title: Int, message: Int, function: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ ->
            function()
        }
        .show()
}