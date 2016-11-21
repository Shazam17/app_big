package com.software.ssp.erkc.modules.drawer

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class DrawerPresenter @Inject constructor(view: IDrawerView) : RxPresenter<IDrawerView>(view), IDrawerPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached() {
        super.onViewAttached()

        showCurrentUser()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
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
        if (activeSession.accessToken == null) {
            view?.setAuthedMenuVisible(false)
            view?.navigateToMainScreen()
            return
        }

        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            view?.showUserInfo(currentUser)
                            view?.setAuthedMenuVisible(true)
                            view?.updateCurrentScreen()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
