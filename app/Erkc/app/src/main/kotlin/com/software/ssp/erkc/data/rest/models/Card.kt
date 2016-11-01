package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

/**
 * @author Alexander Popov on 28/10/2016.
 */
data class Card(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("status_id")
        val statusId: Int,
        @SerializedName("remotecardid")
        var remoteCardId: String,
        @SerializedName("maskcardno")
        var maskCardNo: String,
        @SerializedName("statusstr")
        var statusStr: String

)

data class CardRegistration(
        @SerializedName("id")
        var id: String,
        @SerializedName("url")
        var url: String
)