package com.software.ssp.erkc.modules.request.authedRequest.archiveRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.StatusModel

interface IArchiveRequestListView: IListView<RealmRequest> {
    fun openFilterAlert(listStatus: ArrayList<StatusModel>)
    fun navigateToRequestDetails(requestId: Int)
}

interface IArchiveRequestListPresenter: IListPresenter<RealmRequest, IArchiveRequestListView> {
    fun onRequestClick(request: RealmRequest)
    val selectStatus: ArrayList<Boolean>
    val ownerStatusLabels: ArrayList<String>
    var arrayStatus: ArrayList<StatusModel>
    fun onFilterClick()
    fun onRefreshClick()
}