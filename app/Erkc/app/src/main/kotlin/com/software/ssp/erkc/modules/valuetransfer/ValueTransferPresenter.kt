package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.models.Receipt
import javax.inject.Inject

class ValueTransferPresenter @Inject constructor(view: IValueTransferView) : RxPresenter<IValueTransferView>(view), IValueTransferPresenter {

    override fun onViewAttached() {
        super.onViewAttached()
    }

    override fun onSwipeToRefresh() {

    }

    override fun onItemClick(item: Receipt) {

    }
}
