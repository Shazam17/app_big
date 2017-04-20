package com.software.ssp.erkc.data.rest

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.extensions.getParameters
import com.software.ssp.erkc.extensions.md5
import okhttp3.*
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*


class ErkcInterceptor(val gson: Gson, val activeSession: ActiveSession, val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {

        if (activeSession.isOfflineSession) {
            throw IOException(context.getString(R.string.offline_mode_error))
        }

        try {
            val originalRequest = chain!!.request()
            val originalBody = originalRequest.body()

            val token = activeSession.accessToken ?: activeSession.appToken
            val app_id = Constants.API_OAUTH_CLIENT_ID

            val authorizedRequest: Request = when {
                token == null -> originalRequest
                originalBody != null && originalBody is FormBody -> getSignedFormBodyRequest(token, app_id, originalRequest)
                else -> getSignedGetRequest(token, app_id, originalRequest)
            }

            val response = chain.proceed(authorizedRequest)

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
            throw IOException(context.getString(R.string.error_no_connection), e)
        }
    }

    private fun getSignedGetRequest(token: String, app_id: String, originalRequest: Request): Request {
        val authorizedRequest: Request
        val originalUrl = originalRequest.url()
        val paramNames = originalUrl.queryParameterNames().toMutableList()
        val params = ArrayList<String>()

        paramNames.forEach {
            name ->
            params.addAll(originalUrl.queryParameterValues(name).map { value -> "$name=$value" })
        }

        params.add(1, "token=$token")  // order is important here
        params.add(1, "app_id=$app_id")
        val sig = getSig(token, params)

        val authorizedUrl = originalUrl.newBuilder()
                .addQueryParameter("token", token)
                .addQueryParameter("app_id", app_id)
                .addQueryParameter("sig", sig)
                .build()

        authorizedRequest = originalRequest.newBuilder()
                .url(authorizedUrl)
                .build()

        return authorizedRequest
    }

    private fun getSignedFormBodyRequest(token: String, app_id: String, originalRequest: Request): Request {

        val methodValue = originalRequest.url().queryParameter("method") ?: ""
        val postParams = (originalRequest.body() as FormBody).getParameters()

        val params = mutableListOf<String>()
        for ((paramName, paramValue) in postParams) {
            params.add("$paramName=$paramValue")
        }

        params.add(0, "token=$token")  // order is important here
        params.add(0, "app_id=$app_id")
        params.add(0, "method=$methodValue")
        val sig = getSig(token, params)

        val authorizedBodyBuilder = FormBody.Builder()
                .add("token", token)
                .add("app_id", app_id)
                .add("sig", sig)

        for ((paramName, paramValue) in postParams) {
            authorizedBodyBuilder.add(paramName, paramValue)
        }

        val authorizedRequest = originalRequest.newBuilder()
                .post(authorizedBodyBuilder.build())
                .build()

        return authorizedRequest
    }


    private fun getSig(tokenValue: String?, parameters: List<String>): String {
        val private_key = Constants.API_SIG_PRIVATE_KEY
        val params = parameters.joinToString("")
        val sig = (tokenValue + params + private_key).md5()
        return sig
    }
}
