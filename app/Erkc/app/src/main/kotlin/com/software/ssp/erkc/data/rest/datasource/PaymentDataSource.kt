package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Payment
import com.software.ssp.erkc.data.rest.models.PaymentCheck
import com.software.ssp.erkc.data.rest.models.PaymentInit
import okhttp3.ResponseBody
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
    fun getByReceipt(@Query("token") token: String, @Query("code") code: String): Observable<List<Payment>>

    @GET("?method=payments.getcheck")
    fun getCheck(@Query("token") token: String, @Query("id") id: String): Observable<PaymentCheck>
}
