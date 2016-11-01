package com.software.ssp.erkc.data.rest

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.data.rest.models.ApiResponse
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.net.SocketTimeoutException


class ErkcInterceptor(val gson: Gson) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        try {
            val request = chain!!.request()
            var response = chain.proceed(request)

            val contentType = response.body().contentType()
            val bodyString = response.body().string()

            if (!bodyString.isNullOrEmpty()) {
                try {
                    val apiResponse = gson.fromJson(bodyString, ApiResponse::class.java)

                    if (apiResponse?.result?.code != 0) {
                        throw ApiException(apiResponse.result.description, apiResponse.result.code)
                    }
                } catch (exception: JsonSyntaxException) {
                    //workaround for html response
                    exception.printStackTrace()
                }
            }

            var body = ResponseBody.create(contentType, bodyString)
            try {
                val responseJson = gson.fromJson(bodyString, JsonElement::class.java)

                if (responseJson != null) {
                    val data = responseJson.asJsonObject.get("data")
                    if (data != null) {
                        body = ResponseBody.create(contentType, data.toString())
                    }
                }
            } catch (exception: JsonIOException) {
                //workaround for html response
                exception.printStackTrace()
            }

            return response.newBuilder().body(body).build()

        } catch(e: SocketTimeoutException) {
            throw IOException("Не получилось установить связь сервером", e)
        }
    }
}