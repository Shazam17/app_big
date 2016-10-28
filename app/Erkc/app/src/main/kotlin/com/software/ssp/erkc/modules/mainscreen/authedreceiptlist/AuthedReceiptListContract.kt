package com.software.ssp.erkc.modules.mainscreen.authedreceiptlist

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IAuthedReceiptListView : IView {
    fun navigateToAddReceiptScreen()
    fun navigateToIPUinputScreen()
    fun navigateToPayScreen()
}

interface IAuthedReceiptListPresenter : IPresenter<IAuthedReceiptListView> {
    fun onPayButtonClick()
    fun onIPUButtonClick()
}