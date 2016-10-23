package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.User
import retrofit2.http.*
import rx.Observable


interface AccountDataSource {

    @GET("?method=users.get")
    fun fetchUserInfo(@Field("token") token: String): Observable<User>

}