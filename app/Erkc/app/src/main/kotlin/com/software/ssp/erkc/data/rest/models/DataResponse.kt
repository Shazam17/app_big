package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class DataResponse<T>(
        @SerializedName("data")
        val data: T)