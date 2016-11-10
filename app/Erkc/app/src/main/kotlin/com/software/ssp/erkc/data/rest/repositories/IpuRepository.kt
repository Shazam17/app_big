package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.IpuDataSource
import com.software.ssp.erkc.data.rest.models.Ipu
import okhttp3.ResponseBody
import rx.Observable
import java.util.*
import javax.inject.Inject


class IpuRepository @Inject constructor(private val ipuDataSource: IpuDataSource) : Repository() {

    fun sendParameters(token: String, code: String, values: HashMap<String, String>): Observable<ResponseBody> {
        values.put("token", token)
        values.put("code", code)
        return ipuDataSource.sendParameters(values).compose(this.applySchedulers<ResponseBody>())
    }

    fun getByReceipt(token: String, code: String): Observable<List<Ipu>> {
        return ipuDataSource.getByReceipt(token, code).compose(this.applySchedulers<List<Ipu>>())
    }
}