package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.AuthData
import com.software.ssp.erkc.data.rest.models.Captcha
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable


interface AuthDataSource {

    @FormUrlEncoded
    @POST("?method=users.authorization")
    fun authenticate(@FieldMap params: Map<String, String>): Observable<AuthData>

    @GET
    fun authenticateApp(@Url url: String, @Query("response_type") responseType: String, @Query("client_id") clientId: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST
    fun fetchAppToken(@Url url: String, @FieldMap params: Map<String, String>): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("?method=users.recover")
    fun recoverPassword( @Field("login") login: String,
                         @Field("email") email: String,
                         @Field("number") number: String
                         ): Observable<AuthData>

    @FormUrlEncoded
    @POST("?method=users.registration")
    fun registration(@FieldMap params: Map<String, String>): Observable<Response<ResponseBody>>

    @GET("?method=sys.captcha")
    fun captcha() : Observable<Captcha>
}