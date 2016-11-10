package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.ReceiptType

fun ReceiptType.getIconResId(): Int {
    when(this){
        ReceiptType.GKU -> return R.color.colorPrimary
        ReceiptType.RENT -> return R.color.colorPrimary
        ReceiptType.WATER -> return R.color.colorPrimary
        ReceiptType.WATER_PRIVATE -> return R.color.colorPrimary
        ReceiptType.WATER_CITY -> return R.color.colorPrimary
        ReceiptType.ANTENNA -> return R.color.colorPrimary
        ReceiptType.DOMOFON -> return R.color.colorPrimary
        ReceiptType.OTHER -> return R.color.colorPrimary
        ReceiptType.HEAT -> return R.color.colorPrimary
        ReceiptType.UK_OTHER -> return R.color.colorPrimary
        ReceiptType.HEAT_FINE -> return R.color.colorPrimary
        ReceiptType.GKU_OTHER -> return R.color.colorPrimary
        ReceiptType.GKU_ERKC -> return R.color.colorPrimary
        ReceiptType.OVERHAUL -> return R.color.colorPrimary
        ReceiptType.OVERHAUL_VILLAGE -> return R.color.colorPrimary
        ReceiptType.OVERHAUL_FINE -> return R.color.colorPrimary
        ReceiptType.GKU_FINE -> return R.color.colorPrimary
        ReceiptType.TEST -> return R.color.colorPrimary
    }
}
