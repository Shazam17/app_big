package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
        @SerializedName("authToken")
        val authToken: String)
