package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

/**
 * @author Alexander Popov on 25/10/2016.
 */
data class Address(
        @SerializedName("id")
        val id: Long,
        @SerializedName("name")
        val name: String
)

data class Streets(
        @SerializedName("street")
        val street: Array<String>)

data class ServiceType(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("opcode") val service_code: String,
        @SerializedName("icon") val icon_base64: String
)

data class IdName(
        val id: String,
        val name: String
)
data class IpuDictionary(
        @SerializedName("ipumestoustan")        val locations: Array<IdName> = arrayOf(),
        @SerializedName("ipuviduslugi")         val service_names: Array<IdName> = arrayOf(),
        @SerializedName("ipuintervalpoverki")   val check_intervals: Array<IdName> = arrayOf(),
        @SerializedName("iputip")               val types: Array<IdName> = arrayOf(),
        @SerializedName("iputipzones")          val tariff_types: Array<IdName> = arrayOf(),
        @SerializedName("ipustatuses")          val statuses: Array<IdName> = arrayOf(),
        @SerializedName("ipuprichinazakr")      val close_reasons: Array<IdName> = arrayOf()
)