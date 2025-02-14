package com.vanshika.donorapp.donate

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DonorsDataClass(
    @PrimaryKey(autoGenerate = true)
    var donorId : Int = 0,
    var donorName : String ?= "",
//    var donorLocation,
    var donationType : Int ?= 0,
    var lastDonationDate : String ?= "",
//    var qrCode
)
