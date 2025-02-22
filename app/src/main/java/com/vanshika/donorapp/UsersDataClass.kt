package com.vanshika.donorapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UsersDataClass(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String ?= "",
    var fcmToken: String ?= ""
)
