package com.vanshika.donorapp.requests

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipientsDataClass(
    @PrimaryKey(autoGenerate = true)
    val recipientId : Int = 0,
    var recipientName : String ?= "",
    var requestedItem : String ?= "",
    var specificRequirement: String?= "",
    var medicineDetail: String?="",
    var moneyDetails: String?= "",
    var location: String ?= "",
    var contact: String ?= "",
    var urgencyLevel : Int = 0
)
