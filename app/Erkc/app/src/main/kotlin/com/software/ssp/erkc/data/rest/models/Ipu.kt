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
        val period: Date,

        //------------------------------------
        //Added in version 5:
        @SerializedName("mesto_ustan_id")
        val install_place_id: String?,

        @SerializedName("tip_id")
        val type_id: String?,

        @SerializedName("model")
        val model: String?,

        @SerializedName("date_poverki")
        val next_check_date: String?,

        @SerializedName("marka")
        val brand: String?,

        @SerializedName("date_ustan")
        val install_date: String?,

        @SerializedName("interval_poverki_id")
        val check_interval_id: String?,

        @SerializedName("tip_tarifnaya_zona_id")
        val type_tariff_id: String?,

        @SerializedName("usluga_id")
        val service_name_id: String?,

        @SerializedName("date_begin")
        val begin_date: String?,

        @SerializedName("byuser")
        var user_registered: String?, //if so - "1"

        //@SerializedName("") noname in current API version
        val status: String?
)
