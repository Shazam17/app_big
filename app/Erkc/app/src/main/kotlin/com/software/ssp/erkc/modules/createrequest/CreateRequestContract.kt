package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.RealmTypeHouse

interface ICreateRequestView: IView {
    fun setFieldByRealmRequest(realmRequest: RealmRequest)
    fun setTypeHouseSpinner(typeHouseList: List<String>)
    fun setCompaniesSpinner(companiesList:List<String>)
    fun setTextAddress(addressText: String)
    fun navigateToSearchAddress()
    fun onFetchCompaniesError()
    fun setDraftData(realmDraft: RealmDraft)

}

interface ICreateRequestPresenter: IPresenter<ICreateRequestView> {
    var requestId: Int?
    fun deleteOldValue(id:String)
    fun onViewLoadWithEditMode()
    fun onAddressFieldClick()
    fun fetchCompanies(fias: String)
    fun saveDraftRequest(draft: RealmDraft)
    fun onTryAgainClicked(fias: String)
    fun fetchDraftData(oldUUID:String)
}