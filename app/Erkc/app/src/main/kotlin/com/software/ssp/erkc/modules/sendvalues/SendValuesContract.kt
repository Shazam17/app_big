package com.software.ssp.erkc.modules.sendvalues

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.rest.models.Receipt

/**
 * @author Alexander Popov on 26/10/2016.
 */
interface ISendValuesView : IView {
    fun close()
    fun showIpu(ipu: RealmIpu)
    fun setProgressVisibility(isVisible: Boolean)
}

interface ISendValuesPresenter : IPresenter<ISendValuesView> {

    var receiptId : String?
    var receipt : Receipt?

    fun onSendValuesClick()

}