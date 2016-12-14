package com.software.ssp.erkc.modules.sendvalues

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesPresenter @Inject constructor(view: ISendValuesView) : RxPresenter<ISendValuesView>(view), ISendValuesPresenter {

    @Inject lateinit var ipuProvider: IpuRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override fun onViewAttached(code: String) {
        view?.setProgressVisibility(true)
        subscriptions += ipuProvider.getByReceipt(code)
                .subscribe({
                    data ->
                    view?.fillData(data)
                    view?.setProgressVisibility(false)
                }, {
                    error ->
                    view?.close()
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onSendValuesClick(code: String, values: HashMap<String, String>) {
        view?.setProgressVisibility(true)
        if (activeSession.accessToken == null) { //todo не забыть убрать после тестирования
            subscriptions += ipuProvider.sendParameters(code, values)
                    .subscribe({
                        response ->
                        view?.setProgressVisibility(false)
                        view?.close()
                    }, {
                        error ->
                        view?.setProgressVisibility(false)
                        view?.showMessage(error.message!!)
                    })
        } else {
            subscriptions += realmRepository.saveOfflineIpu(code, values)
                    .subscribe({
                        view?.setProgressVisibility(false)
                        view?.close()
                    }, {
                        error ->
                        view?.setProgressVisibility(false)
                        view?.showMessage(error.parsedMessage())
                    })
        }
    }
}
