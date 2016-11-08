package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.CardRegistration
import com.software.ssp.erkc.data.rest.models.DataResponse
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

interface CardsDataSource {

    @GET("?method=cards.get")
    fun fetchCards(@Query("token") token: String): Observable<DataResponse<List<Card>>>

    @POST("?method=cards.delete")
    @FormUrlEncoded
    fun deleteCard(@Field("token") token: String, @Field("id") id: String): Observable<ResponseBody>

    @POST("?method=cards.activation")
    @FormUrlEncoded
    fun activation(): Observable<ResponseBody>

    @POST("?method=cards.registration")
    @FormUrlEncoded
    fun registration(@Field("token") token: String, @Field("id") id: String): Observable<DataResponse<CardRegistration>>

    @POST("?method=cards.add")
    @FormUrlEncoded
    fun add(@Field("token") token: String, @Field("name") name: String): Observable<DataResponse<Card>>

    @POST("?method=cards.update")
    @FormUrlEncoded
    fun updateCard(@FieldMap map: Map<String, String>): Observable<ResponseBody>

}
