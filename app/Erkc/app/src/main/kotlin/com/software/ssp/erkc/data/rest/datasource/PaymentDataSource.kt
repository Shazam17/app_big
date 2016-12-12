package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Payment
import com.software.ssp.erkc.data.rest.models.PaymentCheck
import com.software.ssp.erkc.data.rest.models.PaymentInfo
import com.software.ssp.erkc.data.rest.models.PaymentInit
import retrofit2.http.*
import rx.Observable

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface PaymentDataSource {

    @FormUrlEncoded
    @POST("?method=payments.init")
    fun init(@FieldMap params: Map<String, String>): Observable<PaymentInit>

    @GET("?method=payments.get")
    fun getByPaymentId(@Query("id") id: String): Observable<PaymentInfo>

    @GET("?method=payments.getbyuser")
    fun getByUser(): Observable<List<Payment>>

    @GET("?method=payments.getcheck")
    fun getCheck(@Query("id") id: String): Observable<PaymentCheck>
}
