package com.vanshika.donorapp.requests

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipients(
    @PrimaryKey(autoGenerate = true)
    var recipientId : Int = 0,
    var recipientName : String ?= "",
    var requestedItem : String ?= "",
    var urgencyLevel : Int ?= 0,
//    var recipientLocation
)
