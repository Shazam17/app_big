package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Address
import com.software.ssp.erkc.data.rest.models.Streets
import com.software.ssp.erkc.data.rest.repositories.DictionaryRepository
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable
import java.util.*


interface DictionaryDataSource {

    @GET("?method=dictionary.addresses")
    fun fetchAddresses(@Query("token") token: String): Observable<List<Address>>

    @GET("?method=dictionary.streets")
    fun fetchStreets(@Query("token") token: String): Observable<Streets>

}