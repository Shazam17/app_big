package com.software.ssp.erkc.modules.requestdetails

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RequestStatusTypes
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class RequestDetailsPresenter @Inject constructor(view: IRequestDetailsView) : RxPresenter<IRequestDetailsView>(view), IRequestDetailsPresenter {

    @Inject lateinit var realmRepository: RealmRepository

    override var requestId: Int = -1

    override fun onViewAttached() {
        fetchRequestById(requestId = requestId)
    }

    private fun fetchRequestById(requestId: Int) {
        subscriptions += realmRepository.fetchRequestById(id = requestId)
                .subscribe (
                        {   realmRequest ->
                            view?.showRequestDetails(realmRequest)
                            view?.configureBottomFrameLayout(realmRequest.state!!.name!!)
                            view?.visibleNeedMenuItem(realmRequest.state!!.name!!)
                        },
                        {
                            print(it.localizedMessage)
                        }
                )
    }

    override fun onCancelRequestButtonClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSubmitCompleteButtonClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEditMenuItemClick() {
        view?.navigateToEditRequestScreen(requestId = requestId)
    }

    override fun onChatMenuItemClick() {
        view?.navigateToChatScreen()
    }
}