package com.software.ssp.erkc.data.realm.models

import android.content.Context
import com.software.ssp.erkc.data.rest.models.ServiceType
import com.software.ssp.erkc.extensions.iconPath
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.io.File
import java.util.*


open class RealmReceipt(
        @PrimaryKey
        open var id: String = "",
        open var street: String = "",
        open var comimssionAgreed: String? = "",
        open var house: String = "",
        open var apart: String = "",
        open var autoPayMode: Int = 0,
        open var name: String = "",
        open var maxSum: Double = 0.0,
        open var lastPaymentDate: Date? = null,
        open var address: String = "",
        open var serviceCode: Int = 0,
        open var amount: Double = 0.0,
        open var barcode: String = "",
        open var lastIpuTransferDate: Date? = null,
        open var supplierName: String = "",
        open var percent: Double = 0.0,
        open var linkedCard: RealmCard? = null) : RealmObject() {

    fun iconPath(context: Context): String? {
        val service = ServiceType(service_code = serviceCode.toString().padStart(3, '0'))
        val path = service.iconPath(context)
        if (!File(path).exists())
            return null
        else
            return path
    }


//    @Ignore
//    var receiptType: ReceiptType = ReceiptType.TEST
//        get() = parseCode(serviceCode)
//
//    fun parseCode(code: Int): ReceiptType {
//        when (code) {
//            352, 357 -> return ReceiptType.GKU
//            in 451..458 -> return ReceiptType.RENT
//            323, 324 -> return ReceiptType.WATER
//            321 -> return ReceiptType.WATER_CITY
//            322 -> return ReceiptType.WATER_PRIVATE
//            393 -> return ReceiptType.ANTENNA
//            605 -> return ReceiptType.DOMOFON
//            607 -> return ReceiptType.OTHER
//            358 -> return ReceiptType.HEAT
//            353 -> return ReceiptType.UK_OTHER
//            354 -> return ReceiptType.HEAT_FINE
//            355 -> return ReceiptType.GKU_OTHER
//            610 -> return ReceiptType.GKU_ERKC
//            1, 612 -> return ReceiptType.OVERHAUL
//            615 -> return ReceiptType.OVERHAUL_VILLAGE
//            2, 611 -> return ReceiptType.OVERHAUL_FINE
//            350, 351 -> return ReceiptType.GKU_FINE
//            else -> return ReceiptType.TEST
//        }
//    }
}

//enum class ReceiptType {
//    GKU,
//    RENT,
//    WATER,
//    WATER_PRIVATE,
//    WATER_CITY,
//    ANTENNA,
//    DOMOFON,
//    OTHER,
//    HEAT,
//    UK_OTHER,
//    HEAT_FINE,
//    GKU_OTHER,
//    GKU_ERKC,
//    OVERHAUL,
//    OVERHAUL_VILLAGE,
//    OVERHAUL_FINE,
//    GKU_FINE,
//    TEST;
//}
