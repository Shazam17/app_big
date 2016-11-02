package com.software.ssp.erkc.data.rest.datasource

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable


interface FaqDataSource {

    @FormUrlEncoded
    @POST("?method=faq.sendmessage")
    fun sendMessage(@FieldMap params: Map<String, String>): Observable<Response<ResponseBody>>
}