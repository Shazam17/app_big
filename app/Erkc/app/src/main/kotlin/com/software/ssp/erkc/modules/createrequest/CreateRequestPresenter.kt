package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmAddressRequest
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class CreateRequestPresenter @Inject constructor(view: ICreateRequestView) : RxPresenter<ICreateRequestView>(view), ICreateRequestPresenter {
    override var requestId: Int? = null

    @Inject
    lateinit var realmRepository: RealmRepository

    override fun onViewLoadWithEditMode() {
        fetchRequestById(requestId!!)
    }

    override fun onViewAttached() {
        fetchDataForDropdowns()
    }

    private fun fetchDataForDropdowns() {
        subscriptions += realmRepository.fetchTypeHouseList()
                .subscribe { typeHouseList ->
                    view?.setTypeHouseSpinner(typeHouseList)
                }
        // TODO add fetch request types
    }

    private fun fetchRequestById(id: Int) {
        subscriptions += realmRepository.fetchRequestById(id = id)
                .subscribe(
                        {   realmRequest ->
                            view?.setFieldByRealmRequest(realmRequest = realmRequest)

                        },
                        {   error ->
                            error.printStackTrace()
                        }
                )
    }

    override fun selectedNewAddress(fias: String) {
        // TODO fetch list manager company from server
    }

    override fun onAddressFieldClick() {
        view?.navigateToSearchAddress()
    }
}