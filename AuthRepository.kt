package com.example.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.tasks.await
import android.content.Context

class AuthRepository(context: Context) {
    
    init {
        // Safe init checking if FirebaseApp is initialized. 
        // In AI Studio without google-services.json, this allows app to run.
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:1234567890:android:321abc456def7890")
                    .setProjectId("math-mentor-mock")
                    .setApiKey("mock-api-key")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val auth by lazy { FirebaseAuth.getInstance() }

    suspend fun signInAnonymously(): Boolean {
        return try {
            auth.signInAnonymously().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}
