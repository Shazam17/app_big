package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.datasource.RequestDataSource
import com.software.ssp.erkc.data.rest.models.Request
import com.software.ssp.erkc.data.rest.models.RequestAdress
import javax.inject.Inject
import rx.Observable

class RequestRepository @Inject constructor(private val requestDataSource: RequestDataSource, private val activeSession: ActiveSession): Repository() {


    fun fetchRequestList(): Observable<List<Request>> {
        activeSession.flag=true
        return requestDataSource
                .fetchRequestList("http://fon.zayavki.pro/mobile/request/index")
                .compose(this.applySchedulers<List<Request>>())
    }

    fun fetchRequestAdress():Observable<List<RequestAdress>>{
        return requestDataSource
                .fetchRequestAdress()
                .compose(this.applySchedulers<List<RequestAdress>>())
    }
}