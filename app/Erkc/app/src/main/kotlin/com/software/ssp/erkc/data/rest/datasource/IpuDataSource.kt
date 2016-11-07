package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.DataResponse
import com.software.ssp.erkc.data.rest.models.Ipu
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable


interface IpuDataSource {

    @FormUrlEncoded
    @POST("?method=ipu.sendmeters")
    fun sendParameters(@FieldMap params: Map<String, String>): Observable<ResponseBody>

    @GET("?method=ipu.getbyreceipt")
    fun getByReceipt(@Query("token") token: String, @Query("code") code: String): Observable<DataResponse<List<Ipu>>>
}