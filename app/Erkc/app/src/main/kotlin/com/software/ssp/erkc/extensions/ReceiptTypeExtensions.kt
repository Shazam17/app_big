package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.ReceiptType

fun ReceiptType.getIconResId(): Int {
    when(this){
        ReceiptType.GKU -> return R.drawable.ic_card_gku
        ReceiptType.RENT -> return R.drawable.ic_card_rent
        ReceiptType.WATER -> return R.drawable.ic_card_water
        ReceiptType.WATER_PRIVATE -> return R.drawable.ic_card_water_private
        ReceiptType.WATER_CITY -> return R.drawable.ic_card_water_city
        ReceiptType.ANTENNA -> return R.drawable.ic_card_antenna
        ReceiptType.DOMOFON -> return R.drawable.ic_card_domofon
        ReceiptType.OTHER -> return R.drawable.ic_card_other
        ReceiptType.HEAT -> return R.drawable.ic_card_heat
        ReceiptType.UK_OTHER -> return R.drawable.ic_card_uk_other
        ReceiptType.HEAT_FINE -> return R.drawable.ic_card_heat_fine
        ReceiptType.GKU_OTHER -> return R.drawable.ic_card_gku_other
        ReceiptType.GKU_ERKC -> return R.drawable.ic_card_gku_erkc
        ReceiptType.OVERHAUL -> return R.drawable.ic_card_overhaul
        ReceiptType.OVERHAUL_VILLAGE -> return R.drawable.ic_card_overhaul_village
        ReceiptType.OVERHAUL_FINE -> return R.drawable.ic_card_overhaul_fine
        ReceiptType.GKU_FINE -> return R.drawable.ic_card_gku_fine
        ReceiptType.TEST -> return R.color.colorPrimary
    }
}
