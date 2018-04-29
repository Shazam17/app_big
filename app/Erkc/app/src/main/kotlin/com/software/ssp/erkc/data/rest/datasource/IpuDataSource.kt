package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Ipu
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable


interface IpuDataSource {

    @FormUrlEncoded
    @POST("?method=ipu.sendmeters")
    fun sendParameters(@FieldMap params: Map<String, String>): Observable<ResponseBody>

    @GET("?method=ipu.getbyreceipt")
    fun getByReceipt(@Query("code") code: String): Observable<List<Ipu>>

    @GET("?method=ipu.gethistorybyreceipt")
    fun getHistoryByReceipt(@Query("code") code: String): Observable<List<Ipu>>

    @FormUrlEncoded
    @POST("?method=ipu.addbyuser")
    fun addByUser(@FieldMap params: Map<String, String>): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("?method=ipu.updatebyuser")
    fun updateByUser(@FieldMap params: Map<String, String>): Observable<ResponseBody>
}
