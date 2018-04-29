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
    fun setData(data: Presenter.UserIPUData)
    fun askDeleteIPUAndData(): Observable<Boolean>
    fun validateDataBeforeCommit(): Boolean
    fun setupFilters()
    fun showProgress(show: Boolean)
}

interface IModulePresenter : IPresenter<IModuleView> {

    enum class FilterType{
        FILTER_LOCATION,
        FILTER_SERVICE_NAME,
        FILTER_CHECK_INTERVAL,
        FILTER_TYPE,
        FILTER_TYPE_TARIFF,
        FILTER_STATUS,
        FILTER_CLOSE_REASON
    }

    var receiptId : String?
    var ipu_number : String?

    fun commitClicked()
    fun delete()
    fun filter(type: FilterType): List<String>
}