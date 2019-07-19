package com.software.ssp.erkc.modules.requestdetails

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.RealmRequestStatus
import io.realm.RealmList

interface IRequestDetailsView: IView {
    fun navigateToEditRequestScreen(requestId: Int)
    fun navigateToChatScreen()
    fun showSelectImagesList()
    fun showStatusesList()
    fun showRequestDetails(realmRequest: RealmRequest)
    fun configureBottomFrameLayout(statusType: String)
    fun createStatusAdapter(statusList: RealmList<RealmRequestStatus>)
    fun visibleNeedMenuItem(statusType: String)
}

interface IRequestDetailsPresenter : IPresenter<IRequestDetailsView> {
    var requestId: Int
    fun onCancelRequestButtonClick()
    fun onSubmitCompleteButtonClick()
    fun onEditMenuItemClick()
    fun onChatMenuItemClick()
}