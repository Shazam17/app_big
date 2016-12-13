package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import retrofit2.http.*
import rx.Observable

interface ReceiptsDataSource {

    @GET("?method=receipts.get")
    fun fetchReceiptInfo(@QueryMap params: Map<String, String>): Observable<Receipt>

    @GET("?method=receipts.getbyuser")
    fun fetchReceipts(): Observable<List<Receipt>>

    @GET("?method=receipts.deletebyuser")
    fun deleteReceipt(@QueryMap params: Map<String, String>): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("?method=receipts.updatebyuser")
    fun updateReceipt(@FieldMap params: Map<String, String>): Observable<ApiResponse>

    @FormUrlEncoded
    @POST("?method=receipts.delsettingbyuser")
    fun clearReceiptSettings(@Field("id") id: String): Observable<ApiResponse>
}
