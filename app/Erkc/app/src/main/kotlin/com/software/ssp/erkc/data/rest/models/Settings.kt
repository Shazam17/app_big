package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName


class Settings(
        @SerializedName("statusoperations")
        val operationsNotificationStatus: Int,

        @SerializedName("getnews")
        val newsNotificationStatus: Int,

        @SerializedName("needtopay")
        val paymentRemindStatus: Int,

        @SerializedName("needtosendmeters")
        val ipuRemindStatus: Int
)