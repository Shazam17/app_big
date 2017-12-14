package com.software.ssp.erkc.modules.setupfastauth

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject


class SetupFastAuthPresenter @Inject constructor(view: ISetupFastAuthView) : RxPresenter<ISetupFastAuthView>(view), ISetupFastAuthPresenter {

    override fun onViewAttached() {
        super.onViewAttached()
    }
}
