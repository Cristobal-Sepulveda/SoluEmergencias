package com.example.soluemergencias.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

@Suppress("LABEL_NAME_CLASH")
class AppRepository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): AppDataSource {

    private val cloudDB = FirebaseFirestore.getInstance()

}
