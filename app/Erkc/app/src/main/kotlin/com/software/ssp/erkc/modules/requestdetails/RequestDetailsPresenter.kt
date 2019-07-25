package com.software.ssp.erkc.modules.requestdetails

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RequestStatusTypes
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class RequestDetailsPresenter @Inject constructor(view: IRequestDetailsView) : RxPresenter<IRequestDetailsView>(view), IRequestDetailsPresenter {

    @Inject
    lateinit var realmRepository: RealmRepository
    @Inject
    lateinit var requestRepository: RequestRepository
    override var requestId: Int = -1

    override fun onViewAttached() {
        updateRequestModel(requestId)
    }

    override fun updateRequestModel(id: Int) {
        view?.setVisibleProgressBar(isVisible = true)
        subscriptions += requestRepository.fetchRequestById(id = id)
                .concatMap { realmRepository.saveRequestById(it) }
                .subscribe(
                        {
                            fetchRequestById(requestId = requestId)

                        },
                        { error ->
                            fetchRequestById(id)
                            error.printStackTrace()
                            print(error.localizedMessage)
                            view?.setVisibleProgressBar(isVisible = false)
                        })
    }

    private fun fetchRequestById(requestId: Int) {
        subscriptions += realmRepository.fetchRequestById(id = requestId)
                .subscribe(
                        { realmRequest ->
                            view?.showRequestDetails(realmRequest)
                            view?.showSelectImagesList(realmRequest.comment!!)
                            view?.configureBottomFrameLayout(realmRequest.state!!.name!!)
                            view?.visibleNeedMenuItem(realmRequest.state!!.name!!)
                            view?.setVisibleProgressBar(isVisible = false)
                        },
                        {
                            print(it.localizedMessage)
                            view?.setVisibleProgressBar(isVisible = false)
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
        view?.navigateToChatScreen(requestId = requestId)
    }
}