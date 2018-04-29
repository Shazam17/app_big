package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.IpuDataSource
import com.software.ssp.erkc.data.rest.models.Ipu
import com.software.ssp.erkc.modules.useripu.Presenter.UserIPUData
import okhttp3.ResponseBody
import rx.Observable
import java.text.SimpleDateFormat
import java.util.*
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
    
    private fun addField(params: MutableMap<String,String>, key: String, value: String) {
        if (value.length > 0) params.put(key, value)
    }

    private fun userIpuParams(code: String, data: UserIPUData): MutableMap<String,String> {
        val params = mutableMapOf("code" to code)

        addField(params, "nomer", data.number)
        addField(params, "mesto_ustan_id", data.locationId())
        addField(params, "usluga_id", data.serviceNameId())
        addField(params, "marka", data.brand)
        addField(params, "model", data.model)
        addField(params, "interval_poverki_id", data.checkIntervalId())
        addField(params, "tip_id", data.typeId())
        addField(params, "tip_tarifnaya_zona_id", data.typeTariffId())
        addField(params, "date_begin", data.begin_date)
        addField(params, "date_ustan", data.install_date)
        addField(params, "date_poverki", data.next_check_date)

        return params
    }

    fun addByUser(code: String, data: UserIPUData): Observable<ResponseBody> {
        return ipuDataSource.addByUser(userIpuParams(code, data)).compose(this.applySchedulers<ResponseBody>())
    }

    fun updateByUser(code: String, data: UserIPUData): Observable<ResponseBody> {
        val params = userIpuParams(code, data)
        addField(params, "id", data.id)

        return ipuDataSource.updateByUser(params).compose(this.applySchedulers<ResponseBody>())
    }

    fun removeByUser(code: String, data: UserIPUData): Observable<ResponseBody> {
        val params = mutableMapOf("code" to code)
        fun today() = SimpleDateFormat("dd.MM.yyyy").format(Date())

        addField(params, "id", data.id)
        addField(params, "nomer", data.number)
        addField(params, "mesto_ustan_id", data.locationId())
        addField(params, "usluga_id", data.serviceNameId())
        addField(params, "status_id", "2") //2 - nonactive
        addField(params, "date_end", today())
        addField(params, "prichina_id", "2") //4 - error reason; 2 - other

        return ipuDataSource.updateByUser(params).compose(this.applySchedulers<ResponseBody>())
    }

}
