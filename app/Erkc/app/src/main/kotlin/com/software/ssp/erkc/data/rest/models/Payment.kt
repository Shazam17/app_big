package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.DateTimeDeserializer
import com.software.ssp.erkc.data.rest.adapters.DateTimeDeserializerPayments
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

enum class PaymentStatus {
    NONE,
    SUCCESS,
    ERROR
}

data class Payment(
        @SerializedName("id")
        var id: String,
        @JsonAdapter(DateTimeDeserializerPayments::class)
        @SerializedName("date_pay")
        var date: Date,
        @SerializedName("amount")
        var amount: Double,
        @SerializedName("pdffile")
        var checkFile: String,
        @SerializedName("status")
        var status: Int,
        @SerializedName("erdesc")
        var errorDesc: String?,
        @SerializedName("receipt_id")
        var receiptId: String,
        @SerializedName("method_id")
        var methodId: String?,
        @SerializedName("operation_id")
        var operationId: String)

data class PaymentInfo(
        @JsonAdapter(DateTimeDeserializer::class)
        @SerializedName("date_pay")
        var date: Date,
        @SerializedName("house")
        var house: String,
        @SerializedName("status")
        var status: Int,
        @SerializedName("street")
        var street: String,
        @SerializedName("barcode")
        var barcode: String,
        @SerializedName("operation_id")
        var operationId: String,
        @SerializedName("summ")
        var summ: Double,
        @SerializedName("supplier_name")
        var supplierName: String,
        @SerializedName("service_name")
        var serviceName: String,
        @SerializedName("amount")
        var amount: Double,
        @SerializedName("text")
        var text: String,
        @SerializedName("id")
        var id: String,
        @SerializedName("address")
        var address: String,
        @SerializedName("receipt_id")
        var receiptId: String,
        @SerializedName("apart")
        var apart: String
)
