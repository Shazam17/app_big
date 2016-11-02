package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.db.StreetCache
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class SearchAddressPresenter @Inject constructor(view: ISearchAddressView) : RxPresenter<ISearchAddressView>(view), ISearchAddressPresenter {

    @Inject lateinit var realmRepo: RealmRepository

    override fun onViewAttached() {
        super.onViewAttached()
        subscriptions += realmRepo.getAllStreets()
                .subscribe({
                    streets ->
                    view?.showData(streets)
                })

    }

    override fun onItemSelected(street: StreetCache) {
        view?.navigateToDrawer(street)
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
}