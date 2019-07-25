package com.software.ssp.erkc.data.rest.repositories

import android.net.Uri
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
import java.net.URI

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

    fun sendComment(requestId: Int, message: String = "", imagePath: String? = null) : Observable<Comment> {
        activeSession.flag=1
        var multipart_body_part: MultipartBody.Part? = null

        val messageParam = RequestBody.create(MediaType.parse("multipart/form-data"), message)
        val requestIdParam = RequestBody.create(MediaType.parse("multipart/form-data"), requestId.toString())

        if (imagePath != null) {

            val file = File(imagePath)
            val request_file = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            multipart_body_part = MultipartBody.Part.createFormData("file_upload", file.name, request_file)
        }
        return requestDataSource.sendComment(requestId = requestIdParam, message = messageParam, url = "http://fon.zayavki.pro/mobile/request/post-comment", file = multipart_body_part)
                .compose(this.applySchedulers<Comment>())
    }
}