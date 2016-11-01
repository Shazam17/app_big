package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.receipt.ReceiptType


fun ReceiptType.getResId(): Int {
    when(this){
        ReceiptType.WATER -> return R.color.colorPrimary
        ReceiptType.POWER -> return R.color.colorRed
    }
}