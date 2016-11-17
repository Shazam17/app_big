package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

/**
 * @author Alexander Popov on 17/11/2016.
 */
data class PaymentInit(
        @SerializedName("id")
        var id: String,
        @SerializedName("url")
        var url: String
)