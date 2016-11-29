package com.software.ssp.erkc.data.rest.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.software.ssp.erkc.Constants
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Alexander Popov on 07/11/2016.
 */
class DatePeriodDeserializer : JsonDeserializer<Date> {
    private val authDateFormat = SimpleDateFormat(Constants.PERIOD_DATE_FORMAT_API, Locale.ENGLISH)

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return authDateFormat.parse(json?.asString)
    }
}

class DateTimeDeserializer : JsonDeserializer<Date> {
    private val authDateFormat = SimpleDateFormat(Constants.DATE_TIME_FORMAT_API, Locale.ENGLISH)

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return authDateFormat.parse(json?.asString)
    }
}