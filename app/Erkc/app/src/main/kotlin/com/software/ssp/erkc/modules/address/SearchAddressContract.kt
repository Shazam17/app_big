package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.db.StreetCache

/**
 * @author Alexander Popov on 25/10/2016.
 */
interface ISearchAddressView : IView {
    fun navigateToDrawer(street: StreetCache)
    fun showData(streets: List<StreetCache>)
}

interface ISearchAddressPresenter : IPresenter<ISearchAddressView> {
    fun onItemSelected(street: StreetCache)
    fun onQuery(query: String)
}