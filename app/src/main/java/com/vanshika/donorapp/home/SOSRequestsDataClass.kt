package com.vanshika.donorapp.home

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SOSRequestsDataClass(
    @PrimaryKey(autoGenerate = true)
    var SosId : Int = 0,
//    var location,
    var itemType : Int ?= 0,
    var timestamp : String ?= ""
)
