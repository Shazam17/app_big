package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.archiveRequestList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmRequest
import javax.inject.Inject

class ArchiveRequestListPresenter @Inject constructor(view: IArchiveRequestListView) : RxPresenter<IArchiveRequestListView>(view), IArchiveRequestListPresenter {



    override fun onRequestClick(request: RealmRequest) {
        view?.navigateToRequestDetails(requestId = request.id)
    }

    override fun onFilterClick() {

    }

    override fun onRefreshClick() {

    }

    override fun onSwipeToRefresh() {

    }

}