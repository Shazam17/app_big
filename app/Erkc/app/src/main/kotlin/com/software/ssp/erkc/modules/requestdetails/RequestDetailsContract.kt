package com.software.ssp.erkc.modules.requestdetails

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IRequestDetailsView: IView {
    fun navigateToEditRequestScreen()
    fun navigateToChatScreen()
    fun showSelectImagesList()
    fun showStatusesList()
    fun configureBottomFrameLayout(isWorkStatus: Boolean)
}

interface IRequestDetailsPresenter : IPresenter<IRequestDetailsView> {
    fun onCancelRequestButtonClick()
    fun onSubmitCompleteButtonClick()
    fun onEditMenuItemClick()
    fun onChatMenuItemClick()
}