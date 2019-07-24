package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.datasource.RequestDataSource
import com.software.ssp.erkc.data.rest.models.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject
import rx.Observable
import java.io.File

class RequestRepository @Inject constructor(private val requestDataSource: RequestDataSource, private val activeSession: ActiveSession): Repository() {


    fun fetchRequestList(): Observable<List<Request>> {
        activeSession.flag=0
        return requestDataSource
                .fetchRequestList(url = "http://fon.zayavki.pro/mobile/request/index")
                .compose(this.applySchedulers<List<Request>>())
    }

    fun fetchRequestAddress():Observable<List<RequestAddress>>{
        activeSession.flag=0
        return requestDataSource
                .fetchRequestAddress(url = "http://fon.zayavki.pro/mobile/common/house-by-company")
                .compose(this.applySchedulers<List<RequestAddress>>())
    }

    fun fetchRequestStates(): Observable<List<StateRequest>> {
        activeSession.flag=0
        return requestDataSource
                .fetchRequestStates(url = "http://fon.zayavki.pro/mobile/common/request-states")
                .compose(this.applySchedulers<List<StateRequest>>())
    }

    fun fetchTypeHouse(): Observable<List<TypeHouse>> {
        activeSession.flag=0
        return requestDataSource
                .fetchTypeHouse(url = "http://fon.zayavki.pro/mobile/common/property-types")
                .compose(this.applySchedulers<List<TypeHouse>>())
    }

    fun fetchRequestById(id:Int):Observable<Request>{
        activeSession.flag=1
        return requestDataSource.fetchRequestById("http://fon.zayavki.pro/mobile/request/$id/view")
                .compose(this.applySchedulers<Request>())
    }

    fun fetchCompaniesList(fias:String):Observable<List<Company>>{
        activeSession.flag=0
        val params= mutableMapOf(
                "fias" to fias
        )
        return requestDataSource.fetchCompanies("http://fon.zayavki.pro/mobile/common/companies-by-house",params)
                .compose(this.applySchedulers<List<Company>>())
    }

//    fun sendComment(requestId: Int, message: String? = null, file: File? = null) : Observable<ResponseBody> {
//        var multipart_body_part: MultipartBody.Part? = null
//
//        val params = mutableMapOf(
//                "request_id" to requestId,
//                "message" to message
//        )
//        if (file != null) {
//            val request_file = RequestBody.create(MediaType.parse(file.path), file)
//            multipart_body_part = MultipartBody.Part.createFormData("attachmentimage", file.name, request_file)
//        }
//        return requestDataSource.sendComment(params = params, )
//    }
}