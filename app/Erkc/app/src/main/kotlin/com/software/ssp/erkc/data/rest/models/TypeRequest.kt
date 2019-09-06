package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class TypeRequest(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
)