package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.DateDeserializer
import java.util.*

/**
 * @author Alexander Popov on 17/11/2016.
 */
data class PaymentInit(
        @SerializedName("id")
        var id: String,
        @SerializedName("url")
        var url: String
)

enum class PaymentMethod {
    DEFAULT,
    ONE_CLICK
}

data class Payment(
        @SerializedName("id")
        var id: String,
        @JsonAdapter(DateDeserializer::class)
        @SerializedName("date_pay")
        var date: Date,
        @SerializedName("amount")
        var amount: Double,
        @SerializedName("filecheck")
        var checkFile: String,
        @SerializedName("status")
        var status: Int,
        @SerializedName("maskedpan")
        var maskedCardNumber: String,
        @SerializedName("comments")
        var comment: String,
        @SerializedName("ercode")
        var errorCode: String,
        @SerializedName("erdesc")
        var errorDesc: String,
        @SerializedName("method_id")
        var methodId: String) {
    var receiptCode: String? = null
}
