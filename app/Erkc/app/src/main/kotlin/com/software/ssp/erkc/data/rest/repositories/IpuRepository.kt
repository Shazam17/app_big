package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.IpuDataSource
import com.software.ssp.erkc.data.rest.models.Ipu
import com.software.ssp.erkc.modules.useripu.Presenter.UserIPUData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.Observable
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class IpuRepository @Inject constructor(private val ipuDataSource: IpuDataSource) : Repository() {

    private val STATUS_ACTIVE = "1"
    private val STATUS_NON_ACTIVE = "2"

    private val CLOSE_REASON_OTHER = "2"
    private val CLOSE_REASON_ERROR_INPUT = "4"

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
        val params2send = mutableMapOf("id" to data.id)
        params2send.putAll(params) //need save order (request signature problem), yes, it's strange to use map, but it is a legacy for now
        return ipuDataSource.updateByUser(params2send).compose(this.applySchedulers<ResponseBody>())
    }

    fun removeByUser(code: String, data: UserIPUData): Observable<ResponseBody> {
        fun today() = SimpleDateFormat("dd.MM.yyyy").format(Date())

        val params = mutableMapOf("id" to data.id)
        addField(params, "code", code)
        addField(params, "nomer", data.number)
        addField(params, "mesto_ustan_id", data.locationId())
        addField(params, "usluga_id", data.serviceNameId())
        addField(params, "status_id", STATUS_NON_ACTIVE)
        addField(params, "date_end", today())
        addField(params, "prichina_id", CLOSE_REASON_OTHER)

        return ipuDataSource.updateByUser(params).compose(this.applySchedulers<ResponseBody>())
    }

    fun sendImageByMeters(code: String, id: String, value: String, file: File): Observable<ResponseBody> {
//        if (true) {
//            file.delete()
//            file.createNewFile()
//            val fos = FileOutputStream(file)
//            try { fos.write("asdf-test".toByteArray()) } finally { fos.close() }
//        }

        val params = mutableMapOf("code" to code)
        params.put("ipu_$id", value)

        val request_file = RequestBody.create(MediaType.parse(file.path), file)
        val multipart_body_part = MultipartBody.Part.createFormData("imagebymeters", file.name, request_file)

        return ipuDataSource.sendImageByMeters(params, multipart_body_part).compose(this.applySchedulers<ResponseBody>())
    }

}
