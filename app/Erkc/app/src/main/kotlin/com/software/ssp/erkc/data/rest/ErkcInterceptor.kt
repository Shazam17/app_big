package com.software.ssp.erkc.data.rest

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ApiException
import com.software.ssp.erkc.data.realm.models.RealmUser
import com.software.ssp.erkc.data.rest.models.ApiErrorType
import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.repositories.AuthRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.getParameters
import com.software.ssp.erkc.extensions.md5
import com.software.ssp.erkc.modules.fastauth.EnterPinActivity
import okhttp3.*
import timber.log.Timber
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

            var token: String? = null
            val methodValue = originalRequest.url().queryParameter("method") ?: ""

            if (methodValue != "users.authorization" &&
                    methodValue != "users.registration" &&
                    methodValue != "sys.captcha") {

                if (activeSession.accessToken == null) {
                    token = activeSession.appToken
                } else if (activeSession.accessToken?.isEmpty()!!) {
                    val prefs = context.getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
                    token = prefs.getString(AuthRepository.LOCAL_TOKEN, "")
                } else {
                    token = activeSession.accessToken
                }

                if (token.isNullOrEmpty()) {
                    token = activeSession.appToken
                }
            } else {
                token = activeSession.appToken
            }

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
                        if (apiResponse?.result?.code == ApiErrorType.SESSION_EXPIRED) {
                            logout()
                        }
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
                val data:JsonElement
                if (responseJson != null) {
                    //TODO: flag для версии DEBUG, поменять для боевых действий
                    if (!activeSession.flag!!) {
                        data = responseJson.asJsonObject.get("data")
                        activeSession.flag=false
                    }else{
                        data =responseJson.asJsonArray
                    }
                    if (data != null) {
                        body = ResponseBody.create(contentType, data.toString())
                    }
                }
            } catch (exception: JsonIOException) {
                //workaround for html response
                exception.printStackTrace()
            }

            return response.newBuilder().body(body).build()

        } catch (e: SocketTimeoutException) {
            throw IOException(context.getString(R.string.error_no_connection), e)
        }
    }

    private fun getSignedGetRequest(token: String, app_id: String, originalRequest: Request): Request {
        val methodValue = originalRequest.url().queryParameter("method") ?: ""
        var authorizedRequest: Request? = null
        val originalUrl = originalRequest.url()
        val paramNames = originalUrl.queryParameterNames().toMutableList()
        val params = ArrayList<String>()

        paramNames.forEach { name ->
            params.addAll(originalUrl.queryParameterValues(name).map { value -> "$name=$value" })
        }

        if (!activeSession.flag!!) {
            params.add(1, "token=$token")  // order is important here
            params.add(1, "app_id=$app_id")
            fixSignatureParams(methodValue, params)
            val sig = getSig(token, params)
            val authorizedUrl = originalUrl.newBuilder()
                    .addQueryParameter("token", token)
                    .addQueryParameter("app_id", app_id)
                    .addQueryParameter("sig", sig)
                    .build()

            authorizedRequest = originalRequest.newBuilder()
                    .url(authorizedUrl)
                    .build()
        } else {
            //TODO Токен для версии DEBUG сменить в релизе
            authorizedRequest = originalRequest
                    .newBuilder()
                    .addHeader("Authorization", "Basic Z2poV3BUT2lJRlBfTnY4THg4SWNqZ0ItOWxOZ2lwcFE6")
                    .build()
        }
        return authorizedRequest!!
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


        fixSignatureParams(methodValue, params)
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

    private fun fixSignatureParams(method: String, params: MutableList<String>) {
        if (method.equals("ipu.sendimagebymeters")) {
            Timber.w("SEND IMAGE REQUEST intercepted! Correcting signature params...")

            val remove_param = params.filter { it.startsWith("ipu_") }.first()
            params.remove(remove_param)
        }
    }

    private fun getSig(tokenValue: String?, parameters: List<String>): String {
        val private_key = Constants.API_SIG_PRIVATE_KEY
        val params = parameters.joinToString("")
        val sig = (tokenValue + params + private_key).md5()
        return sig
    }

    fun logout() {
        val prefs = context.getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit().putString(AuthRepository.LOCAL_TOKEN, "").apply()
    }
}
