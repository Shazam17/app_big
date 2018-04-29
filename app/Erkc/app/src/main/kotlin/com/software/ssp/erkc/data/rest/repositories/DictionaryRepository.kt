package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.DictionaryDataSource
import com.software.ssp.erkc.data.rest.models.IpuDictionary
import com.software.ssp.erkc.data.rest.models.ServiceType
import com.software.ssp.erkc.data.rest.models.Streets
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class DictionaryRepository @Inject constructor(private val dictionaryDataSource: DictionaryDataSource) : Repository() {
    class DictionaryCacheable (
        val streets: Streets,
        val ipu: List<IpuDictionary>
    )

    fun fetchStreets(): Observable<Streets> {
        return dictionaryDataSource
                .fetchStreets()
                .compose(this.applySchedulers<Streets>())
    }

    fun fetchServiceTypes(): Observable<List<ServiceType>> {
        return dictionaryDataSource
                .fetchServiceTypes()
                .compose(this.applySchedulers<List<ServiceType>>())
    }

    fun fetchIpu(): Observable<List<IpuDictionary>> {
        return dictionaryDataSource
                .fetchIpu()
                .compose(this.applySchedulers<List<IpuDictionary>>())
    }
}
