package com.software.ssp.erkc.modules.processfastauth

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject


class ProcessFastAuthPresenter @Inject constructor(view: IProcessFastAuthView) : RxPresenter<IProcessFastAuthView>(view), IProcessFastAuthPresenter {

    override fun onViewAttached() {
        super.onViewAttached()
    }
}
