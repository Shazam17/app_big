package com.software.ssp.erkc.data.rest

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException

class ErkcInterceptor(val authProvider: AuthProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        try {
            val request: Request?
            if (authProvider.authToken != null) {
                request = chain!!
                        .request()!!
                        .newBuilder()
                        .addHeader("X-Auth-Token", authProvider.authToken)
                        .build()
            } else {
                request = chain!!.request()
            }
            return chain.proceed(request)

        } catch(e: SocketTimeoutException) {
            throw IOException("Не получилось установить связь сервером", e)
        }
    }
}