package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.StatusModel
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ActiveRequestListPresenter @Inject constructor(view: IActiveRequestListView) : RxPresenter<IActiveRequestListView>(view), IActiveRequestListPresenter {


    @Inject
    lateinit var realmRepository: RealmRepository
    @Inject
    lateinit var requestRepository: RequestRepository

    override var requestid: Int = -1

    override fun onViewAttached() {
        super.onViewAttached()
        fetchRequestList()
    }

    private fun fetchRequestList() {
        subscriptions += realmRepository.fetchRequestList()
                        .subscribe(
                                {
                                    val data=it.filter {iter-> iter.state?.stateLabel!="Закрыта" }
                                    if (data.isEmpty()) view?.setVisibleEmptyMessage(isVisible = true) else view?.setVisibleEmptyMessage(isVisible = false)
                                    view?.showData(data)
                                },
                                { error ->
                                    error.printStackTrace()
                                }
                        )

    }

    override fun onRequestClick(request: RealmRequest) {
        view?.navigateToRequestDetails(requestId = request.id, titleRequest = request.name ?: "")
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
    override var arrayStatus: ArrayList<StatusModel> = arrayListOf(StatusModel(false, "Ожидает рассмотрения"), StatusModel(false, "На рассмотрении"), StatusModel(false, "В работе"))

    override fun onFilterClick() {
        view?.openFilterAlert(arrayStatus)
    }

    override fun onRefreshClick() {
        updateRequestList()
    }

    override fun onSwipeToRefresh() {
        updateRequestList()

    }

    private fun updateRequestList(){
        subscriptions+=requestRepository.fetchRequestList()
                .concatMap { realmRepository.saveRequestList(it) }
                .subscribe(
                        {
                            fetchRequestList()
                        }
                )
    }
}