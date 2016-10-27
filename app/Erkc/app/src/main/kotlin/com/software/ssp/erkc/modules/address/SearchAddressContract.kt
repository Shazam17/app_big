package com.software.ssp.erkc.modules.address

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Address
import java.util.*

/**
 * @author Alexander Popov on 25/10/2016.
 */
interface ISearchAddressView : IView {
    fun navigateToDrawer(address: Address)
    fun showData(addresses: ArrayList<Address>)
}

interface ISearchAddressPresenter : IPresenter<ISearchAddressView> {
    fun onItemSelected(address: Address)
    fun onQuery(query: String)
}