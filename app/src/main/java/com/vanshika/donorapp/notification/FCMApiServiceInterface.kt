package com.vanshika.donorapp.notification

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMApiServiceInterface {
    @Headers("Content-Type: application/json")
    @POST("registerToken")
    fun sendToken(@Body request: NotificationRequestDataClass): Call<Void>

    @POST("sendNotification")
    fun sendNotification(@Body request: NotificationRequestDataClass): Call<ResponseBody>
}