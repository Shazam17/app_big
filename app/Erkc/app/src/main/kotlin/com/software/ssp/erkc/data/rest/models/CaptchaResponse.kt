package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

data class CaptchaResponse(
        @SerializedName("image")
        val imageDataBase64: String)