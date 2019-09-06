package com.software.ssp.erkc.modules.requestdetails

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmComment
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.RealmRequestStatus
import com.software.ssp.erkc.data.realm.models.RealmTransitions
import com.software.ssp.erkc.data.rest.models.Comment
import io.realm.RealmList

interface IRequestDetailsView: IView {
    fun navigateToEditRequestScreen(requestId: Int)
    fun navigateToChatScreen(requestId: Int)
    fun showSelectImagesList(photosList: List<RealmComment>)
    fun showStatusesList()
    fun showRequestDetails(realmRequest: RealmRequest)
    fun configureBottomFrameLayout(statusType: String)
    fun createStatusAdapter(statusList: List<RealmTransitions>)
    fun visibleNeedMenuItem(statusType: String)
    fun setVisibleProgressBar(isVisible: Boolean)
    fun openFullScreen(downloadLink:String)
}

interface IRequestDetailsPresenter : IPresenter<IRequestDetailsView> {
    var requestId: Int
    fun openFullScreen(comment: RealmComment)
    fun onCancelRequestButtonClick()
    fun onSubmitCompleteButtonClick()
    fun onEditMenuItemClick()
    fun onChatMenuItemClick()
    fun updateRequestModel(id: Int)
}