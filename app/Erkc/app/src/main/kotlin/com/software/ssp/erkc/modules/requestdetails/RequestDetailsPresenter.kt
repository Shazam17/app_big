package com.software.ssp.erkc.modules.requestdetails

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject

class RequestDetailsPresenter @Inject constructor(view: IRequestDetailsView) : RxPresenter<IRequestDetailsView>(view), IRequestDetailsPresenter {
    override fun onCancelRequestButtonClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSubmitCompleteButtonClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditMenuItemClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChatMenuItemClick() {
        view?.navigateToChatScreen()
    }
}