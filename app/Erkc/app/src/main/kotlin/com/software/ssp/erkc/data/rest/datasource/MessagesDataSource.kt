package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Notification
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable


interface MessagesDataSource {

    @FormUrlEncoded
    @POST("?method=messages.setRead")
    fun setMessageRead(@Field("id") id: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("?method=messages.delete")
    fun deleteMessage(@Field("id") id: String): Observable<ResponseBody>

    @GET("?method=messages.get")
    fun getMessagesByUser(): Observable<List<Notification>>

    @GET("?method=messages.get")
    fun getMessageById(@Query("id") id: String): Observable<Notification>
}
