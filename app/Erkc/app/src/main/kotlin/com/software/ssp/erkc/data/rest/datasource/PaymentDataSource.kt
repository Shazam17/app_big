package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.PaymentInit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface PaymentDataSource {

    @FormUrlEncoded
    @POST("payments.init")
    fun init(@Field("token") token: String, @Field("code") code: String, @Field("method_id") method: Int, @Field("summ") summ: Float): Observable<PaymentInit>

}