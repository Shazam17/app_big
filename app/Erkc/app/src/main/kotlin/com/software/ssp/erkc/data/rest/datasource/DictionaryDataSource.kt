package com.software.ssp.erkc.data.rest.datasource

import com.software.ssp.erkc.data.rest.models.IdName
import com.software.ssp.erkc.data.rest.models.IpuDictionary
import com.software.ssp.erkc.data.rest.models.ServiceType
import com.software.ssp.erkc.data.rest.models.Streets
import retrofit2.http.GET
import rx.Observable


interface DictionaryDataSource {
    @GET("?method=dictionary.streets")
    fun fetchStreets(): Observable<Streets>

    @GET("?method=dictionary.opcodes")
    fun fetchServiceTypes(): Observable<List<ServiceType>>

    @GET("?method=dictionary.ipu")
    fun fetchIpu(): Observable<List<IpuDictionary>>

    @GET("?method=dictionary.ipumestoustan")
    fun fetchIpuLocations(): Observable<List<IdName>>

    @GET("?method=dictionary.ipuviduslugi")
    fun fetchIpuServiceNames(): Observable<List<IdName>>

    @GET("?method=dictionary.ipuintervalpoverki")
    fun fetchIpuCheckIntervals(): Observable<List<IdName>>

    @GET("?method=dictionary.iputip")
    fun fetchIpuTypes(): Observable<List<IdName>>

    @GET("?method=dictionary.iputipzones")
    fun fetchIpuTariffTypes(): Observable<List<IdName>>

    @GET("?method=dictionary.ipustatuses")
    fun fetchIpuStatuses(): Observable<List<IdName>>

    @GET("?method=dictionary.ipuprichinazakr")
    fun fetchIpuCloseReason(): Observable<List<IdName>>

}
