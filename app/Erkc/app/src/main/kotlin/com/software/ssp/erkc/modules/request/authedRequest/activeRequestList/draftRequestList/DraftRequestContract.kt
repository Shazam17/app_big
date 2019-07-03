package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.draftRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmRequest

interface IDraftRequestListView: IListView<RealmRequest> {
    fun navigateToRequestInfo(request: RealmRequest)
}
interface IDraftRequestListPresenter: IListPresenter<RealmRequest, IDraftRequestListView> {
    fun onRequestClick(request: RealmRequest)
    fun onFilterClick()
    fun onRefreshClick()
}