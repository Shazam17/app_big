package com.software.ssp.erkc.modules.drawer

import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.common.OpenCardsEvent
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import javax.inject.Inject


class DrawerPresenter @Inject constructor(view: IDrawerView) : RxPresenter<IDrawerView>(view), IDrawerPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var eventBus: Relay<Any, Any>

    override fun onViewAttached() {
        super.onViewAttached()

        showCurrentUser()

        view?.navigateToMainScreen()
        eventBus.ofType(OpenCardsEvent::class.java)
                .subscribe {
                    view?.navigateToDrawerItem(DrawerItem.CARDS)
                }
    }

    override fun onLogoutClick() {
        activeSession.clear()
        view?.clearUserInfo()
        view?.setAuthedMenuVisible(false)
        view?.navigateToMainScreen()
    }

    override fun onUserProfileClick() {
        view?.navigateToUserProfile()
    }

    override fun onUserProfileUpdated() {
        showCurrentUser()
    }

    private fun showCurrentUser() {
        if (activeSession.user == null) {
            view?.setAuthedMenuVisible(false)
        } else {
            view?.setAuthedMenuVisible(true)
            view?.showUserInfo(activeSession.user!!)
        }
    }
}
