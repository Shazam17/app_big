package com.software.ssp.erkc.modules.confirmbyurl

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/11/2016.
 */
class ConfirmByUrlPresenter @Inject constructor(view: IConfirmByUrlView) : RxPresenter<IConfirmByUrlView>(view), IConfirmByUrlPresenter {

    override fun onBankConfirm() {
        view?.showDoneButton()
    }

    override fun onDoneClick() {
        view?.navigateToResult()
    }

}