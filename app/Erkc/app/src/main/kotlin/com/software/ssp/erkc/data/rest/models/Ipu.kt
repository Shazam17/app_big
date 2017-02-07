package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.DatePeriodDeserializer
import com.software.ssp.erkc.data.rest.adapters.DateTimeDeserializer
import java.util.*


data class Ipu(
        @SerializedName("id")
        var id: String,

        @SerializedName("usluga_name")
        var serviceName: String,

        @SerializedName("usluga_shortname")
        var shortName: String?,

        @SerializedName("nomer")
        var number: String,

        @SerializedName("mesto_ustan")
        var installPlace: String,

        @JsonAdapter(DateTimeDeserializer::class)
        @SerializedName("datecreate")
        var date: Date,

        @SerializedName("ipu_pokaz")
        var value: String,

        @JsonAdapter(DatePeriodDeserializer::class)
        @SerializedName("period")
        val period: Date)
