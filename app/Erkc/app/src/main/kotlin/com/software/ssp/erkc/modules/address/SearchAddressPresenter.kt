package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.models.AddressCache
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class SearchAddressPresenter @Inject constructor(view: ISearchAddressView) : RxPresenter<ISearchAddressView>(view), ISearchAddressPresenter {

    @Inject lateinit var realmRepo: RealmRepository

    override fun onViewAttached() {
        super.onViewAttached()
        view?.showData(realmRepo.getAllAdresses())
    }

    override fun onItemSelected(address: AddressCache) {
        view?.navigateToDrawer(address)
    }

    override fun onQuery(query: String) {
        view?.showData(ArrayList(realmRepo.getAllAddressesByQuesry(query)))
    }

}