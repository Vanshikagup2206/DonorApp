package com.vanshika.donorapp.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HealthRecordsDataClass(
    @PrimaryKey(autoGenerate = true)
    var healthRecordId : Int ?= 0,
    var donorId : Int ?=0,
    var donorWeight : String ?= "",
    var donorBp : String ?= "",
    var donorPulse : String ?= "",
    var donorBloodGroup : String ?= "",
    var donorDonationStreak : String?= "",
    var donorHemoglobin : String= ""
)
