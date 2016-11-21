package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IValueTransferView : IView {
    fun navigateToAddReceiptScreen()
    fun navigateToValueTransferListScreen()
}

interface IValueTransferPresenter : IPresenter<IValueTransferView> {
}

