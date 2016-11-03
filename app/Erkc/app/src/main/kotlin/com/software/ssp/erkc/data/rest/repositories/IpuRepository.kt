package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.IpuDataSource
import okhttp3.ResponseBody
import rx.Observable
import java.util.*
import javax.inject.Inject


class IpuRepository @Inject constructor(private val ipuDataSource: IpuDataSource) : Repository() {

    fun sendmeters(token: String, values: HashMap<String, String>) {

    }

    fun getByReceipt(token: String, code: String) : Observable<ResponseBody> {
        return ipuDataSource.getByReceipt(token, code).compose(this.applySchedulers<ResponseBody>())
    }

}