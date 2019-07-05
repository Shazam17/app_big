package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmRequest

interface IActiveRequestListView:IListView<RealmRequest>{
    fun navigateToRequestDetails(requestId: Int)
}
interface IActiveRequestListPresenter:IListPresenter<RealmRequest, IActiveRequestListView>{
    var requestid: Int
    fun onRequestClick(request: RealmRequest)
    fun onFilterClick()
    fun onRefreshClick()
}