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

class DateTimeDeserializerPayments : JsonDeserializer<Date> {
    private val authDateFormat = SimpleDateFormat(Constants.DATE_TIME_FORMAT_API_PAYMENTS, Locale.ENGLISH)

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return authDateFormat.parse(json?.asString)
    }
}

class DateTimeDeserializerNotifications : JsonDeserializer<Date> {
    private val notificationDateFormat = SimpleDateFormat(Constants.NOTIFICATIONS_FORMAT_API, Locale.ENGLISH)

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return notificationDateFormat.parse(json?.asString)
    }
}

class DateTimeDeserializerReceipts : JsonDeserializer<Date> {
    private val receiptDateFormat = SimpleDateFormat(Constants.RECEIPT_DATE_API_FORMAT, Locale.ENGLISH)

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        return receiptDateFormat.parse(json?.asString)
    }
}

