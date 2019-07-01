package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class Request (
        @SerializedName("id")
        val id:Int,
        @SerializedName("title")
        val title:String?,
        @SerializedName("type")
        val type:String?,
        @SerializedName("state")
        val state:String?
)