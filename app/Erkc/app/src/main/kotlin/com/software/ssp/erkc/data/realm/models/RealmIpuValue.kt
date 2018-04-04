package com.software.ssp.erkc.data.realm.models

import com.software.ssp.erkc.Constants
import io.realm.RealmObject
import java.util.*


open class RealmIpuValue(
        open var id: String = "",
        open var serviceName: String = "",
        open var shortName: String? = "",
        open var number: String = "",
        open var installPlace: String = "",
        open var date: Date? = null,
        open var period: Date? = null,
        open var isSent: Boolean = false,
        open var value: String = "",
        open var userRegistered:Boolean = false) : RealmObject() {

    val ipuType: IpuType
        get() {
            return when {
                shortName == null -> IpuType.UNKNOWN
                shortName!!.contains(Constants.HOT_WATER, true) -> IpuType.HOT_WATER
                shortName!!.contains(Constants.COLD_WATER, true) -> IpuType.COLD_WATER
                shortName!!.contains(Constants.GAS, true) -> IpuType.GAS
                shortName!!.contains(Constants.ELECTRICITY, true) -> IpuType.ELECTRICITY
                else -> IpuType.UNKNOWN
            }
        }
}

enum class IpuType {
    HOT_WATER,
    COLD_WATER,
    GAS,
    ELECTRICITY,
    UNKNOWN
}