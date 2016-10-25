package com.software.ssp.erkc.modules.drawer

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import javax.inject.Inject


class DrawerPresenter @Inject constructor(view: IDrawerView) : RxPresenter<IDrawerView>(view), IDrawerPresenter {

    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached() {
        super.onViewAttached()

        if (activeSession.user == null) {
            view?.setAuthedMenuVisible(false)
        } else {
            view?.setAuthedMenuVisible(true)
            view?.showUserInfo(activeSession.user!!)
        }
    }

    override fun onLogoutClick() {
        activeSession.clear()
        view?.clearUserInfo()
        view?.setAuthedMenuVisible(false)
    }
}
