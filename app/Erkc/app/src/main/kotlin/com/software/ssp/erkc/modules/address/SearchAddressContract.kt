package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmStreet

/**
 * @author Alexander Popov on 25/10/2016.
 */
interface ISearchAddressView : IView {
    fun setResult(street: RealmStreet)
    fun close()
    fun showData(streets: List<RealmStreet>)
}

interface ISearchAddressPresenter : IPresenter<ISearchAddressView> {
    fun onItemSelected(street: RealmStreet)
    fun onQuery(query: String)
    fun onBackClick()
}
