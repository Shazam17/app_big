package com.software.ssp.erkc.modules.request.nonauthedrequest

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.repositories.RequestRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

class RequestNonAuthPresenter @Inject constructor(view: IRequestNonAuthView) : RxPresenter<IRequestNonAuthView>(view), IRequestNonAuthPresenter {

    @Inject
    lateinit var requestRepository: RequestRepository

    override fun fetchCompanies(fias: String) {
        subscriptions += requestRepository.fetchCompaniesList(fias)
                .subscribe(
                        {
                            val data = it.first()
                            view?.setSupportInfo(data.name.toString(), data.fact_address.toString(), data.phone_number.toString())
                        },
                        { error ->
                            error.printStackTrace()
                        }
                )
    }

    override fun onAddressClick() {
        view?.navigateToStreetSelectScreen()
    }

    override fun onStreetSelected(street: String) {
        view?.setStreetField(street = street)
    }

    override fun onCallButtonClick() {
        view?.navigateToCallScreen()
    }

}