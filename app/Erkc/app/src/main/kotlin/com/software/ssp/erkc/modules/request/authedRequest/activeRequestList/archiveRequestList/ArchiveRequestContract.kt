package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.archiveRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmRequest

interface IArchiveRequestListView: IListView<RealmRequest> {
    fun navigateToRequestDetails(requestId: Int)
}

interface IArchiveRequestListPresenter: IListPresenter<RealmRequest, IArchiveRequestListView> {
    fun onRequestClick(request: RealmRequest)
    fun onFilterClick()
    fun onRefreshClick()
}