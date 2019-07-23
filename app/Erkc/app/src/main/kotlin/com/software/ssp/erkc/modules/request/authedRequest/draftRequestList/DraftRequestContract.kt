package com.software.ssp.erkc.modules.request.authedRequest.draftRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.modules.request.IRequestListEmptyMessages

interface IDraftRequestListView: IListView<RealmRequest>, IRequestListEmptyMessages {
    fun navigateToRequestInfo(request: RealmRequest)
}
interface IDraftRequestListPresenter: IListPresenter<RealmRequest, IDraftRequestListView> {
    fun onRequestClick(request: RealmRequest)
    fun onFilterClick()
    fun onRefreshClick()
}