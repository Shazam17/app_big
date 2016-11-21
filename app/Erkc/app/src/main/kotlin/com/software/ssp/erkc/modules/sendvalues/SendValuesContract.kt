package com.software.ssp.erkc.modules.sendvalues

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Ipu
import java.util.*

/**
 * @author Alexander Popov on 26/10/2016.
 */
interface ISendValuesView : IView {
    fun navigateToDrawer()
    fun fillData(data: List<Ipu>)
    fun setProgressVisibility(isVisible: Boolean)
}

interface ISendValuesPresenter : IPresenter<ISendValuesView> {
    fun onSendValuesClick(code: String, values: HashMap<String, String>)
    fun onViewAttached(code: String)
}