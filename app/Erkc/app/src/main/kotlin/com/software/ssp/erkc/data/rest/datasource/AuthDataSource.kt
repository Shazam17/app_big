package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.AuthResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap
import rx.Observable


interface AuthDataSource {

    @GET("authenticate")
    fun authenticate(@QueryMap params: Map<String, String>): Observable<AuthResponse>
}