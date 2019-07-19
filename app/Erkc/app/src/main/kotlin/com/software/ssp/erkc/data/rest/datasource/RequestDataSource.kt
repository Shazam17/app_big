package com.software.ssp.erkc.data.rest.datasource

import rx.Observable
import com.software.ssp.erkc.data.rest.models.Request
import com.software.ssp.erkc.data.rest.models.RequestAdress
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RequestDataSource {

    @POST
    fun fetchRequestAdress():Observable<List<RequestAdress>>

    @GET
    fun fetchRequestList(@Url url:String):Observable<List<Request>>

    @POST
    @FormUrlEncoded
    fun createRequest(@FieldMap map: Map<String, String>): Observable<ResponseBody>

    @POST
    @FormUrlEncoded
    fun addComment(@FieldMap map: Map<String, String>): Observable<ResponseBody>

    @GET
    fun getCommentFile(): Call<ResponseBody>

}