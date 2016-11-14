package com.software.ssp.erkc.data.rest.datasource

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface PaymentDataSource {

    @FormUrlEncoded
    @POST("payments.init")
    fun init(@Field("token") token: String, @Field("code") code: String, @Field("method_id") method: Int, @Field("summ") summ: String)

}