package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.AddressCache

/**
 * @author Alexander Popov on 25/10/2016.
 */
interface ISearchAddressView : IView {
    fun navigateToDrawer(address: AddressCache)
    fun showData(addresses: List<AddressCache>)
}

interface ISearchAddressPresenter : IPresenter<ISearchAddressView> {
    fun onItemSelected(address: AddressCache)
    fun onQuery(query: String)
}