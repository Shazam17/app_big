package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

data class ApiResponse(
        @SerializedName("result")
        val result: ApiError)
