package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class Receipt(

        @SerializedName("street")
        val street: String,

        @SerializedName("mode_id")
        val autoPayMode: Int,

        @SerializedName("service_name")
        val name: String,

        @SerializedName("maxsumma")
        val maxSumm: Double,

        @SerializedName("id")
        val id: String,

        @SerializedName("lastpay")
        val lastPayment: String?,

        @SerializedName("address")
        val address: String,

        @SerializedName("service_code")
        val serviceCode: Int,

        @SerializedName("payment")
        val payment: Double,

        @SerializedName("barcode")
        val barcode: String,

        @SerializedName("lastsendmeteripu")
        val lastValueTransfer: String?,

        @SerializedName("supplier_name")
        val supplierName: String,

        @SerializedName("percent_q")
        val persent: Double,

        @SerializedName("user_card_id")
        val linkedCardId: String?) {

    var receiptType: ReceiptType = ReceiptType.TEST
        get() = parseCode(serviceCode)

    fun parseCode(code: Int): ReceiptType {
        when (code) {
            352, 357 -> return ReceiptType.GKU
            in 451..458 -> return ReceiptType.RENT
            323, 324 -> return ReceiptType.WATER
            321 -> return ReceiptType.WATER_CITY
            322 -> return ReceiptType.WATER_PRIVATE
            393 -> return ReceiptType.ANTENNA
            605 -> return ReceiptType.DOMOFON
            607 -> return ReceiptType.OTHER
            358 -> return ReceiptType.HEAT
            353 -> return ReceiptType.UK_OTHER
            354 -> return ReceiptType.HEAT_FINE
            355 -> return ReceiptType.GKU_OTHER
            350 -> return ReceiptType.TEST
            610 -> return ReceiptType.GKU_ERKC
            612 -> return ReceiptType.OVERHAUL
            615 -> return ReceiptType.OVERHAUL_VILLAGE
            611 -> return ReceiptType.OVERHAUL_FINE
            351 -> return ReceiptType.GKU_FINE
            else -> return ReceiptType.TEST
        }
    }
}

enum class ReceiptType() {
    GKU,
    RENT,
    WATER,
    WATER_PRIVATE,
    WATER_CITY,
    ANTENNA,
    DOMOFON,
    OTHER,
    HEAT,
    UK_OTHER,
    HEAT_FINE,
    GKU_OTHER,
    GKU_ERKC,
    OVERHAUL,
    OVERHAUL_VILLAGE,
    OVERHAUL_FINE,
    GKU_FINE,
    TEST; //TODO REMOVE TEST;
}
