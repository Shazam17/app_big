package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.IpuDataSource
import java.util.*
import javax.inject.Inject


class IpuRepository @Inject constructor(private val ipuDataSource: IpuDataSource) : Repository() {

    fun sendmeters(token: String, values: HashMap<String, String>) {

    }

}