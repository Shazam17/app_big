package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import android.util.Log
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.realm.models.RequestStatusTypes
import com.software.ssp.erkc.data.realm.models.RequestTabTypes
import com.software.ssp.erkc.data.rest.models.Request
import com.software.ssp.erkc.data.rest.models.RequestStatus
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ActiveRequestListPresenter @Inject constructor(view: IActiveRequestListView) : RxPresenter<IActiveRequestListView>(view), IActiveRequestListPresenter {


    @Inject
    lateinit var realmRepository: RealmRepository

    override var requestid: Int = -1

    override fun onViewAttached() {
        super.onViewAttached()
        fetchRequestList()
    }

    private fun fetchRequestList() {
        val statusArray: ArrayList<RequestStatus> = ArrayList()
        statusArray.add(RequestStatus(type = RequestStatusTypes.PendingReview.name, date = "02.06.2019 19:04"))
//        statusArray.add(RequestStatus(type = RequestStatusTypes.OnReview.name, date = "03.06.2019 11:00"))
//        statusArray.add(RequestStatus(type = RequestStatusTypes.InWork.name, date = "03.06.2019 14:24"))
//        statusArray.add(RequestStatus(type = RequestStatusTypes.Complete.name, date = "03.06.2019 18:55"))
        val data: List<Request> = listOf(
                Request(0,
                        "Отсутствие отопления",
                        "Вызов сантехника",
                        typeTab = RequestTabTypes.Active.name,
                        countMessages = 0,
                        description = "Какое-то описание нашей заявки. Возможно много текста. Возможно много текста. Возможно много текста. Возможно много текста.Возможно много текста. Возможно много текста. Возможно много текста",
                        infoAboutProblem = "Какая-то информация о нашей заявке. Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Какая-то информация о нашей заявке. Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Какая-то информация о нашей заявке. Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Какая-то информация о нашей заявке. Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа Инфа",
                        status = statusArray,
                        date = "02.06.2019",
                        number = 3597842,
                        photosPath = null,
                        isCrash = true,
                        nameManagerCompany = "ЖилТомскСтрой",
                        serviceProvider = "ЖКХ",
                        typeStore = "Типичный",
                        address = "Лыткина 18",
                        numberPhone = "89138655197",
                        FIO = "Калюжный Е.Р."
                        )
        )
        realmRepository.saveRequestList(data)
                .concatMap {
                    realmRepository.fetchRequestList()
                }
                .subscribe(
                        {
                            realmRequestList ->
                            view?.showData(realmRequestList)
                        },
                        {
                            error ->
                            error.printStackTrace()
                        }
                )

    }
    override fun onRequestClick(request: RealmRequest) {
        view?.navigateToRequestDetails(requestId = request.id)
    }

    override fun onFilterClick() {
    }

    override fun onRefreshClick() {
    }

    override fun onSwipeToRefresh() {
    }
}
