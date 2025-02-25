package com.vanshika.donorapp.notification

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationRequestDataClass(
//    var token : String ?= ""
    @PrimaryKey(autoGenerate = true)
    var notificationId: Int = 0,
    val tokens: List<String>,
    val title: String ?= "",
    val body: String ?= ""
)
