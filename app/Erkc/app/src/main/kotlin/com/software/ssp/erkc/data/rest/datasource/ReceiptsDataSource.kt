package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import rx.Observable

interface ReceiptsDataSource {

    @GET("?method=receipts.get")
    fun fetchReceiptInfo(@QueryMap params: Map<String, String>): Observable<Receipt>

    @GET("?method=receipts.getbyuser")
    fun fetchReceipts(@Query("token") token: String): Observable<List<Receipt>>

    @GET("?method=receipts.deletebyuser")
    fun deleteReceipt(@QueryMap params: Map<String, String>): Observable<ApiResponse>
}
