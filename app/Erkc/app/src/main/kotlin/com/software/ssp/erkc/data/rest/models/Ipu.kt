package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.DateDeserializer
import com.software.ssp.erkc.data.rest.adapters.DatePeriodDeserializer
import java.util.*

/**
 * @author Alexander Popov on 07/11/2016.
 */
data class Ipu(
        @SerializedName("usluga_name")
        val serviceName: String,
        @SerializedName("mesto_ustan")
        val installPlace: String,
        @JsonAdapter(DatePeriodDeserializer::class)
        @SerializedName("period")
        val period: Date,
        @SerializedName("id")
        val id: String,
        @SerializedName("nomer")
        val number: String)

data class IpuValue(
        @SerializedName("id")
        var id: String,
        @SerializedName("usluga_name")
        var serviceName: String,
        @SerializedName("nomer")
        var number: String,
        @SerializedName("mesto_ustan")
        var installPlace: String,
        @JsonAdapter(DateDeserializer::class)
        @SerializedName("datecreate")
        var date: Date,
        @SerializedName("ipu_pokaz")
        var value: Int) {
    var receiptCode: String? = null
}