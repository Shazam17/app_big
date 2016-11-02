package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.DictionaryDataSource
import com.software.ssp.erkc.data.rest.models.Address
import com.software.ssp.erkc.data.rest.models.Streets
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class DictionaryRepository @Inject constructor(private val dictionaryDataSource: DictionaryDataSource) : Repository() {

    fun fetchAddresses(token: String): Observable<List<Address>> {
        return dictionaryDataSource
                .fetchAddresses(token)
                .compose(this.applySchedulers<List<Address>>())
    }

    fun fetchStreets(token: String): Observable<Streets> {
        return dictionaryDataSource
                .fetchStreets(token)
                .compose(this.applySchedulers<Streets>())
    }

}