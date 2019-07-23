package com.software.ssp.erkc.modules.request.authedRequest.draftRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmRequest

interface IDraftRequestListView: IListView<RealmDraft> {
    fun navigateToRequestInfo(request: RealmDraft)
}
interface IDraftRequestListPresenter: IListPresenter<RealmDraft, IDraftRequestListView> {
    fun onDraftClick(request: RealmDraft)
    fun onFilterClick()
    fun onRefreshClick()
}