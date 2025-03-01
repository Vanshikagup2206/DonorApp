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
    var address: String? = "",
    var age: String? = "",
    var createdDate: String? = "",
    var donationType: String? = "",
    var donationfrequency: String? = "",
    var bloodType: String? = "",
    var number: String? = "",
    val isHealthy: Boolean? = null,
    val traveledRecently: Boolean? = null,
    val tookMedication: Boolean? = null,
    val consumesAlcohol: Boolean? = null,
    val diabities: Boolean? = null,
    val hadRecentSurgery: Boolean? = null,
    val tookRecentVaccine: Boolean? = null,
    val bloodPressur: Boolean? = null,
    val paymentMethod: String? = "Not required",
    val donationMethod: String? = " ",//anonymous or public
    val lattitude: Double,
    val longitude: Double,

)
