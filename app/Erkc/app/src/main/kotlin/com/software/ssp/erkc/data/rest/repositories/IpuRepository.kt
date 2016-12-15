package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.IpuDataSource
import com.software.ssp.erkc.data.rest.models.Ipu
import okhttp3.ResponseBody
import rx.Observable
import java.util.*
import javax.inject.Inject


class IpuRepository @Inject constructor(private val ipuDataSource: IpuDataSource) : Repository() {

    fun sendParameters(code: String, values: HashMap<String, String>): Observable<ResponseBody> {
        values.put("code", code)
        val params = HashMap<String, String>()
        for ((key, value) in values) {
            params.put("ipu_" + key, value)
        }
        return ipuDataSource.sendParameters(params).compose(this.applySchedulers<ResponseBody>())
    }

    fun getByReceipt(code: String): Observable<List<Ipu>> {
        return ipuDataSource.getByReceipt(code).compose(this.applySchedulers<List<Ipu>>())
    }

    fun getHistoryByReceipt(token: String, code: String): Observable<List<Ipu>> {
        return ipuDataSource.getHistoryByReceipt(token, code).compose(this.applySchedulers<List<Ipu>>())
    }
}
