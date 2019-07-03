package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.draftRequestList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmRequest
import javax.inject.Inject

class DraftRequestListPresenter @Inject constructor(view: IDraftRequestListView) : RxPresenter<IDraftRequestListView>(view), IDraftRequestListPresenter {
    override fun onRequestClick(request: RealmRequest) {

    }

    override fun onFilterClick() {

    }

    override fun onRefreshClick() {
    }

    override fun onSwipeToRefresh() {

    }

}