package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class Setting(
        @SerializedName("value")
        val value: Int,

        @SerializedName("param")
        val param: String
)