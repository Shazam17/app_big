package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
class Receipt(
        @SerializedName("street")
        val street: String,

        @SerializedName("house")
        val house: String,

        @SerializedName("apart")
        val apart: String,

        @SerializedName("mode_id")
        val autoPayMode: Int,

        @SerializedName("service_name")
        val name: String,

        @SerializedName("maxsumma")
        val maxSumm: Double,

        @SerializedName("id")
        val id: String?,

        @SerializedName("lastpay")
        val lastPayment: String?,

        @SerializedName("address")
        val address: String,

        @SerializedName("service_code")
        val serviceCode: Int,

        @SerializedName("amount")
        val amount: Double,

        @SerializedName("barcode")
        val barcode: String,

        @SerializedName("lastsendmeteripu")
        val lastValueTransfer: String?,

        @SerializedName("supplier_name")
        val supplierName: String,

        @SerializedName("percent_q")
        val persent: Double,

        @SerializedName("user_card_id")
        val linkedCardId: String?) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Receipt::class.java)
    }
}


enum class AutoPaymentMode{
        OFF,
        AUTO,
        ONE_CLICK
}