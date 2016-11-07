package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.rest.adapters.DatePeriodDeserializer
import java.util.*

/**
 * @author Alexander Popov on 07/11/2016.
 */
data class Ipu(
        @SerializedName("usluga_name")
        val uslugaName: String,
        @SerializedName("mesto_ustan")
        val mestoUstan: String,
        @JsonAdapter(DatePeriodDeserializer::class)
        @SerializedName("period")
        val period: Date,
        @SerializedName("id")
        val id: String,
        @SerializedName("nomer")
        val nomer: String

)