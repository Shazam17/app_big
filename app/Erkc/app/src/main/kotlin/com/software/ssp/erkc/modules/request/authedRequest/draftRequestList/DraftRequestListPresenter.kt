package com.software.ssp.erkc.modules.request.authedRequest.draftRequestList

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.common.UpdateRequestListAdapter
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmDraft
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class DraftRequestListPresenter @Inject constructor(view: IDraftRequestListView) : RxPresenter<IDraftRequestListView>(view), IDraftRequestListPresenter {

    @Inject
    lateinit var realmRepository: RealmRepository
    @Inject
    lateinit var eventBus: Relay<Any, Any>

    override fun onViewAttached() {
        super.onViewAttached()
        fetchDraft()
        subscribeToUpdateEvent()
    }

    private fun fetchDraft() {
        subscriptions += realmRepository.fetchDraftRequestList()
                .subscribe(
                        {
                            view?.setVisibleEmptyMessage(isVisible = it.isEmpty())
                            view?.showData(it)
                        },
                        { error ->
                            view?.showMessage(error.localizedMessage)
                        }
                )
    }

    override fun onDraftClick(request: RealmDraft?) {
        view?.navigateToRequestInfo(request)
    }

    override fun onFilterClick() {

    }

    override fun onRefreshClick() {
    }

    override fun onSwipeToRefresh() {
        view?.setLoadingVisible(false)
    }

    private fun subscribeToUpdateEvent() {
        subscriptions += eventBus.ofType(UpdateRequestListAdapter::class.java)
                .subscribe { event ->
                    fetchDraft()
                }
    }

}