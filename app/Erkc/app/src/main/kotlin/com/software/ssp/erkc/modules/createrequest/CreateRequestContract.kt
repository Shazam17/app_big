package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.RealmTypeHouse

interface ICreateRequestView: IView {
    fun setFieldByRealmRequest(realmRequest: RealmRequest)
    fun setTypeHouseSpinner(typeHouseList: List<RealmTypeHouse>)
    fun setTextAddress(addressText: String)
    fun navigateToSearchAddress()
}

interface ICreateRequestPresenter: IPresenter<ICreateRequestView> {
    var requestId: Int?
    fun onViewLoadWithEditMode()
    fun onAddressFieldClick()
    fun selectedNewAddress(fias: String)
}