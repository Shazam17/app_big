package com.software.ssp.erkc.modules.useripu

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.rest.models.Receipt
import rx.Completable
import rx.Observable


interface IModuleView : IView {
    fun bindData(ipu_data: Presenter.UserIPUData)
    fun close()
    fun setModeAdd()
    fun setModeEdit()
    fun setData(ipu_data: Presenter.UserIPUData)
    fun askDeleteIPUAndData(): Observable<Boolean>
}

interface IModulePresenter : IPresenter<IModuleView> {
    var receiptId : String?
    var ipu_number : String?

    fun commitClicked()
    fun delete()
}