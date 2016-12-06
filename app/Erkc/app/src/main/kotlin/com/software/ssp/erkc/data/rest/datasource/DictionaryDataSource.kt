package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.Streets
import retrofit2.http.GET
import rx.Observable


interface DictionaryDataSource {
    @GET("?method=dictionary.streets")
    fun fetchStreets(): Observable<Streets>
}
