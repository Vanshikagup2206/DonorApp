package com.vanshika.donorapp.requests

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipientsDataClass(
    @PrimaryKey
    var userId: String = "",
    var name: String? = "",
    var requirement: String? = "",
    var bloodOrganRequirement: String? = null,
    var location: String? = "",
    var contactNo: String? = "",
    var urgencyLevel: Int = 1,
    var medicineMoneyDetails: String? = null,
     // to track which user created the request
)
