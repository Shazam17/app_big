package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmStreet
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class SearchAddressPresenter @Inject constructor(view: ISearchAddressView) : RxPresenter<ISearchAddressView>(view), ISearchAddressPresenter {

    @Inject lateinit var realmRepo: RealmRepository

    override fun onViewAttached() {
        onQuery("")
    }

    override fun onItemSelected(street: RealmStreet) {
        view?.setResult(street)
        view?.close()
    }

    override fun onQuery(query: String) {
        subscriptions += realmRepo.getAllStreetsByQuery(query)
                .subscribe({
                    streets ->
                    view?.showData(streets)
                })
    }

    override fun onViewDetached() {
        realmRepo.close()
        super.onViewDetached()
    }

    override fun onBackClick() {
        view?.close()
    }
}