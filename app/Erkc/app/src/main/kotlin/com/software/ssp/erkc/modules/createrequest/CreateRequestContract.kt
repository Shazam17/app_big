package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmLocalImage
import com.software.ssp.erkc.data.realm.models.RealmRequest
import io.realm.RealmList

interface ICreateRequestView : IView {
    fun setFieldByRealmRequest(realmRequest: RealmRequest)
    fun setTypeHouseSpinner(typeHouseList: List<String>)
    fun setTypesRequestSpinner(typesRequestList:List<String>)
    fun setCompaniesSpinner(companiesList: List<String>)
    fun setTextAddress(addressText: String)
    fun navigateToSearchAddress()
    fun onFetchCompaniesError()
    fun setDraftData(realmDraft: RealmDraft)
    fun notifySelectedImagesListDataChange()
    fun showCameraScreen()
    fun showGalleryScreen()
    fun createSelectedImagesAdapter(images: RealmList<RealmLocalImage>)
//    fun openImageFullScreen()
}

interface ICreateRequestPresenter : IPresenter<ICreateRequestView> {
    var requestId: Int?
    fun deleteOldValue(id: String)
    fun setEvent()
    fun onViewLoadWithEditMode()
    fun onAddressFieldClick()
    fun fetchCompanies(fias: String)
    fun saveDraftRequest(draft: RealmDraft)
    fun onTryAgainClicked(fias: String)
    fun fetchDraftData(oldUUID: String)
    fun generateRequest(
            title: String,
            address: String,
            fias: String,
            company: String,
            typeRequest: String,
            typeHouse: String,
            description: String,
            fio: String,
            phoneNum: String,
            images: List<RealmLocalImage>
    )
    fun fetchTypesRequest()
    fun onCameraButtonClick()
    fun onGalleryButtonClick()
    fun onPhotoClick(localImage: RealmLocalImage)
}