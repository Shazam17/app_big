package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.models.User
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import rx.Observable


interface AccountDataSource {

    @GET("?method=users.get")
    fun fetchUserInfo(): Observable<User>

    @POST("?method=users.update")
    fun updateUserInfo(@QueryMap params: Map<String, String>): Observable<ApiResponse>
}
