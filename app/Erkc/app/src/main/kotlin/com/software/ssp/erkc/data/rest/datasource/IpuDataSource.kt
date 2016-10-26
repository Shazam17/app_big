package com.software.ssp.erkc.data.rest.datasource

import okhttp3.ResponseBody
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable


interface IpuDataSource {

    @FormUrlEncoded
    @POST("?method=upu.sendparameters")
    fun authenticate(): Observable<ResponseBody>

    @GET("?method=ipu.getbyreceipt")
    fun getByReceipt(): Observable<ResponseBody>
}