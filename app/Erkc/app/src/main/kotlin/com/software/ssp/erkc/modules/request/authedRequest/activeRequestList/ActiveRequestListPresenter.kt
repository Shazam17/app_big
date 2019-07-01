package com.software.ssp.erkc.modules.request.authedRequest.activeRequestList

import android.util.Log
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmRequest
import com.software.ssp.erkc.data.rest.models.Request
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class ActiveRequestListPresenter @Inject constructor(view: IActiveRequestListView) : RxPresenter<IActiveRequestListView>(view), IActiveRequestListPresenter {


    @Inject
    lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        super.onViewAttached()
        fetchRequestList()
    }

    private fun fetchRequestList() {
        val data: List<Request> = listOf(Request(0, "Отсутствие отопления", "Вызов сантехника", "На рассмотрении"),
                Request(1, "Прорванная труба", "Вызов сантехника", "В работе"),
                Request(2, "Сломанная лампочка в подъезде", "Вызов электрика", "Ожидает рассмотрения"))
        realmRepository.saveRequestList(data)
                .subscribe(
                        {
                            if (it) {
                                Log.e("SAVE DATA", "SUCCESS")
                            } else {
                                Log.e("SAVE DATA", "ERROR")
                            }
                        },
                        {

                        }
                )
//        subscriptions += realmRepository.fetchRequestList()
//                .subscribe(
//                        { realmRequestList ->
//                            view?.showData(realmRequestList)
//                        },
//                        { error ->
//                            error.printStackTrace()
//                        }
//                )

    }
    override fun onRequestClick(request: RealmRequest) {
    }

    override fun onFilterClick() {
    }

    override fun onRefreshClick() {
    }

    override fun onSwipeToRefresh() {
    }
}
