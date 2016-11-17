package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.PaymentInit
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface PaymentDataSource {

    @FormUrlEncoded
    @POST("payments.init")
    fun init(@FieldMap params: Map<String, String>): Observable<PaymentInit>

}