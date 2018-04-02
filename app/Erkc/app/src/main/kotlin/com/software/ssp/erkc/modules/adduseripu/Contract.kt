package com.software.ssp.erkc.modules.adduseripu

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Receipt


interface IModuleView : IView {
    fun bindData(ipu_data: Presenter.UserIPUData)
}

interface IModulePresenter : IPresenter<IModuleView> {
    var receiptId : String?
    var receipt : Receipt?

    fun commitClicked()
}