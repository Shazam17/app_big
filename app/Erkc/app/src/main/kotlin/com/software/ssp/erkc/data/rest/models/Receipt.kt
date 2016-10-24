package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class Receipt (
        @SerializedName("supplier_name")
        val supplierName: String,
        @SerializedName("amount")
        val amount: String,
        @SerializedName("service_name")
        val serviceName: String,
        @SerializedName("address")
        val address: String,
        @SerializedName("barcode")
        val barcode: String)
