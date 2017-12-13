package com.software.ssp.erkc.modules.createpin

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject


class CreatePinPresenter @Inject constructor(view: ICreatePinView) : RxPresenter<ICreatePinView>(view), ICreatePinPresenter {

    override fun onViewAttached() {
        super.onViewAttached()
    }
}
