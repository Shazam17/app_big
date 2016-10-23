package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

data class ApiError(
        @SerializedName("desc")
        val description: String,
        @SerializedName("code")
        val code: Int)
