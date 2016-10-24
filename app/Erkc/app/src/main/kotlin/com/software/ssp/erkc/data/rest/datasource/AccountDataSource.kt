package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.User
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable


interface AccountDataSource {

    @GET("?method=users.get")
    fun fetchUserInfo(@Query("token") token: String): Observable<User>

}