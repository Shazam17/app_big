package com.software.ssp.erkc.extensions

import org.json.JSONException
import org.json.JSONObject
import retrofit2.adapter.rxjava.HttpException
import java.net.UnknownHostException

private const val UNKNOWN_ERROR_MESSAGE = "Unknown error"
private const val NO_INTERNET_CONNECTION_ERROR_MESSAGE = "Нет соединения с Интернетом"

fun Throwable.parsedMessage(): String {
    if (this is HttpException) {
        val responseString = response().errorBody().string()

        val errorJson: JSONObject
        try {
            errorJson = JSONObject(responseString)
        } catch (exception: JSONException) {
            return UNKNOWN_ERROR_MESSAGE
        }

        return errorJson.optString("message", UNKNOWN_ERROR_MESSAGE)
    } else if (this is UnknownHostException) {
        return NO_INTERNET_CONNECTION_ERROR_MESSAGE
    }

    return message ?: UNKNOWN_ERROR_MESSAGE
}