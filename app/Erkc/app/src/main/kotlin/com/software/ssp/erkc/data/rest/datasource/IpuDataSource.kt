package com.software.ssp.erkc.data.rest.datasource

import okhttp3.ResponseBody
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable


interface IpuDataSource {

    @FormUrlEncoded
    @POST("?method=upu.sendparameters")
    fun authenticate(): Observable<ResponseBody>

    @GET("?method=ipu.getbyreceipt")
    fun getByReceipt(@Query("token") token: String, @Query("code") code: String): Observable<ResponseBody>
}