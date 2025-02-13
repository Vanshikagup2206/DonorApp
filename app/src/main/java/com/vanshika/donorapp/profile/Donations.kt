package com.vanshika.donorapp.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Donations(
    @PrimaryKey(autoGenerate = true)
    var donorId : Int =0,
    var recipientId : Int ?= 0,
    var itemType : String ?= "",
    var donationStatus : Int ?= 0,
    var donationDate : String ?= ""
)
