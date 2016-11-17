package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
class Receipt(
        @SerializedName("amount")
        val amount: Double,
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
        val barcode: String,
        @SerializedName("user_card_id")
        val userCardId: String?= null) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Receipt::class.java)
    }
}
