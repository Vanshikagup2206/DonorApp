package com.vanshika.donorapp.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: FCMApiServiceInterface by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.43.185:3000/") // ✅ Apne server ka IP yahan update karein
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FCMApiServiceInterface::class.java)
    }
}
