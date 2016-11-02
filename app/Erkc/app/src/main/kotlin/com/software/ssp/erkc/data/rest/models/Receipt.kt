package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
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
        val barcode: String) : PaperParcelable {
        companion object {
                @JvmField val CREATOR = PaperParcelable.Creator(Receipt::class.java)
        }
}
