package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.DataResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import retrofit2.http.GET
import retrofit2.http.QueryMap
import rx.Observable

interface ReceiptsDataSource {

    @GET("?method=receipts.get")
    fun fetchReceiptInfo(@QueryMap params: Map<String, String>): Observable<DataResponse<Receipt>>
}
