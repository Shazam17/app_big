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

enum class PaymentMethod {
    DEFAULT,
    ONE_CLICK
}

data class Payment(
        @SerializedName("id")
        var id: String,
        @SerializedName("date_pay")
        var date: String,
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