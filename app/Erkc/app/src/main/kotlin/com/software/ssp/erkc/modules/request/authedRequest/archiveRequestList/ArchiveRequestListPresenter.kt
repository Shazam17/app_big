package com.software.ssp.erkc.modules.request.authedRequest.archiveRequestList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.StatusModel
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ArchiveRequestListPresenter @Inject constructor(view: IArchiveRequestListView) : RxPresenter<IArchiveRequestListView>(view), IArchiveRequestListPresenter {

    @Inject
    lateinit var realmRepository: RealmRepository
    @Inject
    lateinit var requestRepository: RequestRepository

    override fun onViewAttached() {
        super.onViewAttached()
        fetchRequestList()
    }

    private fun fetchRequestList() {
        subscriptions += realmRepository.fetchRequestList()
                .subscribe(
                        {
                            val data = it.filter { iter -> iter.state?.stateLabel != "Новая" }
                            view?.showData(data)
                        },
                        { error ->
                            error.printStackTrace()
                        }
                )

    }

    override fun onRequestClick(request: RealmRequest) {
        view?.navigateToRequestDetails(requestId = request.id)
    }

    override val selectStatus: ArrayList<Boolean>
        get() {
            val array = ArrayList<Boolean>()
            arrayStatus.forEach { status -> array.add(status.isChecked) }
            return array
        }
    override val ownerStatusLabels: ArrayList<String>
        get() {
            val arrayLabels = ArrayList<String>()
            arrayStatus.forEach { status -> arrayLabels.add(status.label) }
            return arrayLabels
        }
    override var arrayStatus: ArrayList<StatusModel> = arrayListOf(StatusModel(false, "Выполнено"), StatusModel(false, "Отменено"))

    override fun onFilterClick() {
        view?.openFilterAlert(arrayStatus)
    }

    override fun onRefreshClick() {
        updateRequestList()

    }

    override fun onSwipeToRefresh() {
        updateRequestList()

    }

    private fun updateRequestList() {
        subscriptions += requestRepository.fetchRequestList()
                .concatMap { realmRepository.saveRequestList(it) }
                .subscribe(
                        {
                            fetchRequestList()
                        }
                )
    }
}