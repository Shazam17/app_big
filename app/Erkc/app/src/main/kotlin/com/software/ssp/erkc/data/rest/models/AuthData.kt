package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

data class AuthData(
        @SerializedName("refresh_token")
        val refresh_token: String,
        @SerializedName("access_token")
        val access_token: String,
        @SerializedName("expires_in")
        val expires_in: Long)
