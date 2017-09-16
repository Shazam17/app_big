package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.DateTimeDeserializerReceipts
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import java.util.*

@PaperParcel
class Receipt(
        @SerializedName("street")
        val street: String,

        @SerializedName("commission_agreed")
        val comimssionAgreed: String,

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

        @JsonAdapter(DateTimeDeserializerReceipts::class)
        @SerializedName("lastpay")
        val lastPaymentDate: Date?,

        @SerializedName("address")
        val address: String,

        @SerializedName("service_code")
        val serviceCode: Int,

        @SerializedName("amount")
        val amount: Double,

        @SerializedName("barcode")
        val barcode: String,

        @JsonAdapter(DateTimeDeserializerReceipts::class)
        @SerializedName("lastsendmeteripu")
        val lastIpuTransferDate: Date?,

        @SerializedName("supplier_name")
        val supplierName: String,

        @SerializedName("percent_q")
        val percent: Double,

        @SerializedName("user_card_id")
        val linkedCardId: String?) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Receipt::class.java)
    }
}
