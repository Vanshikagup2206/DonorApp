package com.vanshika.donorapp.requests

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipientsDataClass(
    @PrimaryKey
    val recipientId: String = "",
    val name: String? = "",
    val requirement: String? = "",
    val bloodOrganRequirement: String? = null,
    val location: String? = "",
    val contactNo: String? = "",
    val urgencyLevel: Int = 1,
    val medicineMoneyDetails: String? = null,
    val userId: String = "" // to track which user created the request
)
