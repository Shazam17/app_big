package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IValueTransferView : IView {
    fun navigateToNewValueTransferScreen()
    fun navigateToValueTransferListScreen()
}

interface IValueTransferPresenter : IPresenter<IValueTransferView> {
}

