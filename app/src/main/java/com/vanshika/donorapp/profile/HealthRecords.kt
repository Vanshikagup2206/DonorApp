package com.vanshika.donorapp.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HealthRecords(
    @PrimaryKey(autoGenerate = true)
    var healthRecordId : Int = 0,
    var donorId : Int ?= 0,
    var donorWeight : Float ?= 0F,
    var donorBp : Int ?= 0,
    var medicalHistory : String ?= ""
)
