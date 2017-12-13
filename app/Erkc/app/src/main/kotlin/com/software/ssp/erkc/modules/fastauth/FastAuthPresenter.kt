package com.software.ssp.erkc.modules.fastauth

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject


class FastAuthPresenter @Inject constructor(view: IFastAuthView) : RxPresenter<IFastAuthView>(view), IFastAuthPresenter {

    override fun onViewAttached() {
        super.onViewAttached()
    }
}
