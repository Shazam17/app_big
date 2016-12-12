package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.CardActivation
import com.software.ssp.erkc.data.rest.models.CardRegistration
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

/**
 * @author Alexander Popov on 10/11/2016.
 */
interface CardsDataSource {

    @GET("?method=cards.get")
    fun fetchCards(): Observable<List<Card>>

    @GET("?method=cards.get")
    fun fetchCard(@Query("id") id: String): Observable<Card>

    @POST("?method=cards.delete")
    @FormUrlEncoded
    fun deleteCard(@Field("id") id: String): Observable<ResponseBody>

    @POST("?method=cards.activation")
    @FormUrlEncoded
    fun activation(@Field("id") id: String): Observable<CardActivation>

    @POST("?method=cards.registration")
    @FormUrlEncoded
    fun registration(@Field("id") id: String): Observable<CardRegistration>

    @POST("?method=cards.add")
    @FormUrlEncoded
    fun add(@Field("name") name: String): Observable<Card>

    @POST("?method=cards.update")
    @FormUrlEncoded
    fun updateCard(@FieldMap map: Map<String, String>): Observable<ResponseBody>
}
