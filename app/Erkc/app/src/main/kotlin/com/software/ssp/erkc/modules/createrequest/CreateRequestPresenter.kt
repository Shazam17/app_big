package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class CreateRequestPresenter @Inject constructor(view: ICreateRequestView) : RxPresenter<ICreateRequestView>(view), ICreateRequestPresenter {
    override var requestId: Int? = null

    @Inject
    lateinit var realmRepository: RealmRepository

    override fun onViewLoadWithEditMode() {
        fetchRequestById(requestId!!)
    }

    private fun fetchRequestById(id: Int) {
        subscriptions += realmRepository.fetchRequestById(id = id)
                .subscribe(
                        {   realmRequest ->
                            view?.setFieldByRealmReqeust(realmRequest = realmRequest)

                        },
                        {   error ->
                            error.printStackTrace()
                        }
                )
    }


}