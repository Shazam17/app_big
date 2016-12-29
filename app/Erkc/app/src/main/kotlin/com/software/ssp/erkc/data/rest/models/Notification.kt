package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.DateTimeDeserializerNotifications
import java.util.*


class Notification(
        @SerializedName("id")
        val id: String,

        @SerializedName("isread")
        val isRead: Int,

        @SerializedName("message")
        val message: String,

        @SerializedName("theme")
        val title: String,

        @JsonAdapter(DateTimeDeserializerNotifications::class)
        @SerializedName("date")
        val date: Date,

        @JsonAdapter(DateTimeDeserializerNotifications::class)
        @SerializedName("dtisread")
        val readDate: Date?
)
