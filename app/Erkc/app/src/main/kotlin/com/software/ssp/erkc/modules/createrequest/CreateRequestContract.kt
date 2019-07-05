package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmRequest

interface ICreateRequestView: IView {
    fun setFieldByRealmReqeust(realmRequest: RealmRequest)
}

interface ICreateRequestPresenter: IPresenter<ICreateRequestView> {
    var requestId: Int?
    fun onViewLoadWithEditMode()
}