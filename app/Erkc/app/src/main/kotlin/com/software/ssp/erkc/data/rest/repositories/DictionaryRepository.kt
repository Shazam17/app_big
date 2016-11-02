package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.DictionaryDataSource
import com.software.ssp.erkc.data.rest.models.Address
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class DictionaryRepository @Inject constructor(private val dictionaryDataSource: DictionaryDataSource) : Repository() {

    fun fetchAddresses(token: String): Observable<List<Address>> {
        return dictionaryDataSource
                .fetchAdresses(token)
                .compose(this.applySchedulers<List<Address>>())
    }

}