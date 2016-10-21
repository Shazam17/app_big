package com.software.ssp.erkc.modules.drawer

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject


class DrawerPresenter @Inject constructor(view: IDrawerView) : RxPresenter<IDrawerView>(view), IDrawerPresenter {
}