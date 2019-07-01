package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmRequest

interface IActiveRequestListView:IListView<RealmRequest>{
    fun navigateToRequestInfo(request: RealmRequest)

}
interface IActiveRequestListPresenter:IListPresenter<RealmRequest,IActiveRequestListView>{
    fun onRequestClick(request: RealmRequest)
    fun onFilterClick()
    fun onRefreshClick()
}