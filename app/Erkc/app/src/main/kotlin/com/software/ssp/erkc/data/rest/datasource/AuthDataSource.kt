package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.AuthData
import com.software.ssp.erkc.data.rest.models.DataResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable


interface AuthDataSource {

    @FormUrlEncoded
    @POST("?method=users.authorization")
    fun authenticate(@Field("token") token: String, @Field("login") login: String, @Field("password") password: String): Observable<DataResponse<AuthData>>

    @GET
    fun authenticateApp(@Url url: String, @Query("response_type") responseType: String, @Query("client_id") clientId: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST
    fun fetchAppToken(@Url url: String, @FieldMap params: Map<String, String>): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("?method=users.registration")
    fun registration(
            @Field("token") token: String,
            @Field("name") name: String,
            @Field("login") login: String,
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("repassword") repassword: String
    ): Observable<Response<ResponseBody>>

}