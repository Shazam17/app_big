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

    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        showAllStreets()
    }

    override fun onItemSelected(street: RealmStreet) {
        view?.setResult(street)
        view?.close()
    }

    override fun onQuery(query: String) {
        subscriptions += realmRepository
                .fetchStreets(query)
                .subscribe(
                        {
                            streets ->
                            view?.showData(streets)
                        }
                )
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onBackClick() {
        view?.close()
    }

    private fun showAllStreets() {
        subscriptions += realmRepository.fetchStreets()
                .subscribe(
                        {
                            streets ->
                            view?.showData(streets)
                        }
                )
    }
}
