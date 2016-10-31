package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class Receipt(
        @SerializedName("amount")
        val amount: String,
        @SerializedName("mode_id")
        val autoPayMode: String?,
        @SerializedName("supplier_name")
        val supplierName: String,
        @SerializedName("service_name")
        val serviceName: String,
        @SerializedName("address")
        val address: String,
        @SerializedName("id")
        val id: String?,
        @SerializedName("barcode")
        val barcode: String)
