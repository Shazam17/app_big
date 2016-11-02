package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.db.AddressCache
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
        subscriptions += realmRepo.getAllAddresses()
                .subscribe({
                    addresses ->
                    view?.showData(addresses)
                })

    }

    override fun onItemSelected(address: AddressCache) {
        view?.navigateToDrawer(address)
    }

    override fun onQuery(query: String) {
        subscriptions += realmRepo.getAllAddressesByQuery(query)
                .subscribe({
                    addresses ->
                    view?.showData(addresses)
                })

    }

    override fun onViewDetached() {
        realmRepo.close()
        super.onViewDetached()
    }
}