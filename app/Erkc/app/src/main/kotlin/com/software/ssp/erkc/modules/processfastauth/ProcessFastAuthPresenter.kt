package com.software.ssp.erkc.modules.processfastauth

import android.provider.Settings.Global.getString
import com.jakewharton.rxrelay.Relay
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.securepreferences.SecurePreferences
import com.software.ssp.erkc.common.Logout
import com.software.ssp.erkc.common.LogoutFinished
import com.software.ssp.erkc.common.OpenHistoryWithReceiptEvent
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.AuthRepository


class ProcessFastAuthPresenter @Inject constructor(view: IProcessFastAuthView) : RxPresenter<IProcessFastAuthView>(view), IProcessFastAuthPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var eventBus: Relay<Any, Any>

    private var doubleBackToExitPressedOnce = false

    override fun onViewAttached() {
        super.onViewAttached()
        subscribeToLogoutEvent()
    }

    override fun onChangeUserClick() {
        view?.setPin("")
        eventBus.call(Logout())
    }

    private fun subscribeToLogoutEvent() {
        subscriptions += eventBus.ofType(LogoutFinished::class.java)
                .subscribe {
                    event ->
                    view?.finishFastAuthActivity()
                }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            System.exit(0)
        } else {
            doubleBackToExitPressedOnce = true
            view?.showMessage(R.string.on_back_pressed_text)
            subscriptions += Observable.timer(2, TimeUnit.SECONDS)
                    .subscribe({
                        doubleBackToExitPressedOnce = false
                    })
        }
    }
}
