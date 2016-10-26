package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.models.Address
import io.realm.Case
import io.realm.Realm
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 25/10/2016.
 */
class SearchAddressPresenter @Inject constructor(view: ISearchAddressView) : RxPresenter<ISearchAddressView>(view), ISearchAddressPresenter {


    override fun onViewAttached() {
        super.onViewAttached()
        val realm = Realm.getDefaultInstance()
        val results = realm.where(Address::class.java).findAll()
        view?.showData(ArrayList(results))
    }

    override fun onItemSelected(address: Address) {
        view?.navigateToDrawer(address)
    }

    override fun onQuery(query: String) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(Address::class.java).contains("query", query.toLowerCase()).findAll()
        view?.showData(ArrayList(results))
    }

}