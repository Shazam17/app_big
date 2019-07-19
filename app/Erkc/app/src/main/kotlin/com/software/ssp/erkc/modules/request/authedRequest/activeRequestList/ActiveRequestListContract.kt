package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.StatusModel

interface IActiveRequestListView:IListView<RealmRequest>{
    fun openFilterAlert(listStatus: ArrayList<StatusModel>)
    fun navigateToRequestDetails(requestId: Int)
}
interface IActiveRequestListPresenter:IListPresenter<RealmRequest, IActiveRequestListView>{
    val selectStatus: ArrayList<Boolean>
    val ownerStatusLabels: ArrayList<String>
    var arrayStatus: ArrayList<StatusModel>
    fun onFilterClick()
    var requestid: Int
    fun onRequestClick(request: RealmRequest)
    fun onRefreshClick()
}