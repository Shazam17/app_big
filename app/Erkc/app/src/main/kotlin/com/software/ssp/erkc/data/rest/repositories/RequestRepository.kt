package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.datasource.RequestDataSource
import com.software.ssp.erkc.data.rest.models.*
import javax.inject.Inject
import rx.Observable

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
}