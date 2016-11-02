package com.software.ssp.erkc.modules.mainscreen

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IMainScreenView: IView {
    fun showNonAuthedScreen()
    fun showAddReceiptScreen()
    fun showReceiptListScreen()
}

interface IMainScreenPresenter: IPresenter<IMainScreenView>{

}