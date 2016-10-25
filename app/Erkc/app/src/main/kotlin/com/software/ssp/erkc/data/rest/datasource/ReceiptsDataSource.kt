package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.DataResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface ReceiptsDataSource {

    @GET("?method=receipts.get")
    fun fetchReceiptInfo(@Query("token") token: String,
                         @Query("code") code: String,
                         @Query("street") street: String,
                         @Query("house") house: String,
                         @Query("apart") apart: String): Observable<DataResponse<Receipt>>

}
