package com.vanshika.donorapp.notification

import android.util.Log
import okhttp3.*
import java.io.IOException
import com.google.firebase.messaging.FirebaseMessagingService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token: $token")
        registerTokenToServer(token) // Register token to your server
    }

    private fun registerTokenToServer(token: String) {
        val url = "http://192.168.43.185:3000/registerToken"
        val json = """{"token": "$token"}"""

        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "Token registration failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("FCM", "Token registered successfully!")
            }
        })
    }
}