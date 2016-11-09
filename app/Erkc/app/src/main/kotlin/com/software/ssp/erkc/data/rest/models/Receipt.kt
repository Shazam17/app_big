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
        val barcode: String,
        @SerializedName("service_code")
        val serviceCode: Int,
        @SerializedName("lastpay")
        val lastPayment: String?,
        @SerializedName("lastsendmeteripu")
        val lastValueTransfer: String?) {

    var receiptType: ReceiptType = ReceiptType.TEST
        get() = receiptType.parseCode(serviceCode)
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

    fun parseCode(code: Int): ReceiptType {
        when (code) {
            352, 357 -> return GKU
            in 451..458 -> return RENT
            323, 324 -> return WATER
            321 -> return WATER_CITY
            322 -> return WATER_PRIVATE
            393 -> return ANTENNA
            605 -> return DOMOFON
            607 -> return OTHER
            358 -> return HEAT
            353 -> return UK_OTHER
            354 -> return HEAT_FINE
            355 -> return GKU_OTHER
            350 -> return TEST
            610 -> return GKU_ERKC
            612 -> return OVERHAUL
            615 -> return OVERHAUL_VILLAGE
            611 -> return OVERHAUL_FINE
            351 -> return GKU_FINE
            else -> return TEST
        }
    }
}
