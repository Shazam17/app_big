package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Address
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable


interface DictionaryDataSource {

    @GET("?method=dictionary.addresses")
    fun fetchAdresses(@Query("token") token: String): Observable<List<Address>>

}