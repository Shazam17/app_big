package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RequestDataSource {

    @POST
    fun fetchRequestAddress(@Url url: String): Observable<List<RequestAddress>>

    @GET
    fun fetchRequestList(@Url url: String): Observable<List<Request>>

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

    @GET
    fun fetchRequestById(@Url url: String): Observable<Request>

    @POST
    fun fetchCompanies(@Url url: String, @QueryMap params: Map<String, String>): Observable<List<Company>>

    @Multipart
    @POST
    fun sendComment(
            @Url url: String,
            @Part file: MultipartBody.Part?,
            @Part("request_id") requestId: RequestBody,
            @Part("message") message: RequestBody
    ) : Observable<Comment>

}