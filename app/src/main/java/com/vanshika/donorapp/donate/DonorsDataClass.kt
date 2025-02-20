package com.vanshika.donorapp.donate

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.Date

@Entity
data class DonorsDataClass(
    @PrimaryKey(autoGenerate = true)
    var donorId: Int = 0,
    var donorName: String? = "",
    var gender: String? = "",
    var address : String ?= "",
    var age: String? = "",
//    var createddate: Date ?= null,
    var donationType: String? = "",
    var donationfrequency: String? = "",
    var bloodType: String? = "",
    var number: String? = "",

    )
