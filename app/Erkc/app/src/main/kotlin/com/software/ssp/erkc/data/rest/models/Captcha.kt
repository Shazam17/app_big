package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.Base64Deserializer

/**
 * @author Alexander Popov on 31/10/2016.
 */
data class Captcha(
        @JsonAdapter(Base64Deserializer::class)
        @SerializedName("image")
        val image: ByteArray
)