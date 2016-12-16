package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Payment
import com.software.ssp.erkc.data.rest.models.PaymentCheck
import com.software.ssp.erkc.data.rest.models.PaymentInfo
import com.software.ssp.erkc.data.rest.models.PaymentInit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import rx.Observable

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface PaymentDataSource {

    @POST("?method=payments.init")
    fun init(@QueryMap params: Map<String, String>): Observable<PaymentInit>

    @GET("?method=payments.get")
    fun getPaymentsById(@Query("id") id: String): Observable<PaymentInfo>

    @GET("?method=payments.getbyuser")
    fun getUserPayments(): Observable<List<Payment>>

    @GET("?method=payments.getcheck")
    fun getCheck(@Query("id") id: String): Observable<PaymentCheck>
}
