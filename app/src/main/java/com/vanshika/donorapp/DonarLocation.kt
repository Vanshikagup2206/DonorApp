package com.vanshika.donorapp

import androidx.room.ColumnInfo

data class DonarLocation(
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
)

