package com.vanshika.donorapp.map

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    fun searchLocation(
        @Query("format") format: String = "json",
        @Query("q") query: String
    ): Call<List<PlaceResponse>>
}