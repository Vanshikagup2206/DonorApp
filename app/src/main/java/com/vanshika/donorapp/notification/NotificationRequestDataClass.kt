package com.vanshika.donorapp.notification

data class NotificationRequestDataClass(
//    var token : String ?= ""
    val tokens: List<String>,
    val title: String ?= "",
    val body: String ?= ""
)
