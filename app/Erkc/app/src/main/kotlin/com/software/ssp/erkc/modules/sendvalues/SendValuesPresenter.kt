package com.software.ssp.erkc.modules.sendvalues

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesPresenter @Inject constructor(view: ISendValuesView) : RxPresenter<ISendValuesView>(view), ISendValuesPresenter {

    @Inject lateinit var ipuProvider: IpuRepository
    @Inject lateinit var activeSession: ActiveSession

    override fun onViewAttached(code: String) {
        view?.setProgressVisibility(true)
        subscriptions += ipuProvider.getByReceipt(activeSession.accessToken!!, code)
                .subscribe({
                    data ->
                    view?.fillData()
                    view?.setProgressVisibility(false)
                }, {
                    error ->
                    view?.setProgressVisibility(false)
                    view?.showMessage(error.message!!)
                })
    }

    override fun onSendValuesClick(values: HashMap<String, String>) {
    }

}