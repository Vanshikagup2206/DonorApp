package com.vanshika.donorapp.requests

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class `Hospitals DataClass`(
    @PrimaryKey(autoGenerate = true)
    var hospitalId : Int = 0,
    var hospitalName : String ?= "",
//    var hospitalLocation,
    var hospitalContact : String ?= "",
    var availableResources : String ?= ""
)
