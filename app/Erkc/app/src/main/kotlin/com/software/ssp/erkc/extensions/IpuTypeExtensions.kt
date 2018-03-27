package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.IpuType

fun IpuType.getIconResId(): Int {
    return when (this) {
        IpuType.HOT_WATER -> R.drawable.pic_hot_water
        IpuType.COLD_WATER -> R.drawable.pic_cold_water
        IpuType.GAS -> R.drawable.pic_gas
        IpuType.ELECTRICITY -> R.drawable.pic_electro
        IpuType.UNKNOWN -> 0
    }
}

fun IpuType.getUnitResId(): Int {
    return when (this) {
        IpuType.HOT_WATER -> R.string.history_value_water_unit
        IpuType.COLD_WATER -> R.string.history_value_water_unit
        IpuType.GAS -> R.string.history_value_water_unit
        IpuType.ELECTRICITY -> R.string.history_value_electro_unit
        IpuType.UNKNOWN -> R.string.none
    }
}
fun IpuType.getUnitResIdOnly(): Int {
    return when (this) {
        IpuType.HOT_WATER -> R.string.history_value_water_unit_only
        IpuType.COLD_WATER -> R.string.history_value_water_unit_only
        IpuType.GAS -> R.string.history_value_water_unit_only
        IpuType.ELECTRICITY -> R.string.history_value_electro_unit_only
        IpuType.UNKNOWN -> R.string.none
    }
}
