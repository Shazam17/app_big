package com.software.ssp.erkc.modules.splash

import com.software.ssp.erkc.common.mvp.RxPresenter
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Alexander Popov on 23.10.2016.
 */
class SplashPresenter @Inject constructor(view: ISplashView) : RxPresenter<ISplashView>(view), ISplashPresenter {

    override fun onViewAttached() {
        super.onViewAttached()
        subscriptions += Observable.timer(5, TimeUnit.SECONDS)
                .subscribe({
                    view?.navigateToSignIn()
                })
    }

}