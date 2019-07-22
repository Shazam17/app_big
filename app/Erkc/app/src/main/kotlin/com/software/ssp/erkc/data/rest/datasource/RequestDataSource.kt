package com.software.ssp.erkc.data.rest.datasource

import rx.Observable
import com.software.ssp.erkc.data.rest.models.Request
import com.software.ssp.erkc.data.rest.models.RequestAddress
import com.software.ssp.erkc.data.rest.models.StateRequest
import com.software.ssp.erkc.data.rest.models.TypeHouse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RequestDataSource {

    @POST
    fun fetchRequestAddress(@Url url: String):Observable<List<RequestAddress>>

    @GET
    fun fetchRequestList(@Url url:String):Observable<List<Request>>

    @GET
    fun fetchRequestStates(@Url url: String): Observable<List<StateRequest>>

    @GET
    fun fetchTypeHouse(@Url url: String): Observable<List<TypeHouse>>

    @POST
    @FormUrlEncoded
    fun createRequest(@FieldMap map: Map<String, String>): Observable<ResponseBody>

    @POST
    @FormUrlEncoded
    fun addComment(@FieldMap map: Map<String, String>): Observable<ResponseBody>

    @GET
    fun getCommentFile(): Call<ResponseBody>

}