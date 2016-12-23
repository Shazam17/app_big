package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.IpuDataSource
import com.software.ssp.erkc.data.rest.models.Ipu
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject


class IpuRepository @Inject constructor(private val ipuDataSource: IpuDataSource) : Repository() {

    fun sendParameters(code: String, values: MutableMap<String, String>): Observable<ResponseBody> {
        val params = mutableMapOf("code" to code)

        for ((key, value) in values.toSortedMap()) {
            params.put("ipu_" + key, value)
        }

        return ipuDataSource.sendParameters(params).compose(this.applySchedulers<ResponseBody>())
    }

    fun getByReceipt(code: String): Observable<List<Ipu>> {
        return ipuDataSource.getByReceipt(code).compose(this.applySchedulers<List<Ipu>>())
    }

    fun getHistoryByReceipt(code: String): Observable<List<Ipu>> {
        return ipuDataSource.getHistoryByReceipt(code).compose(this.applySchedulers<List<Ipu>>())
    }
}
