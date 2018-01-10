package com.software.ssp.erkc.modules.sendvalues

import android.util.ArrayMap
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.*
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 26/10/2016.
 */
class SendValuesPresenter @Inject constructor(view: ISendValuesView) : RxPresenter<ISendValuesView>(view), ISendValuesPresenter {

    @Inject lateinit var ipuRepository: IpuRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override var receipt: Receipt? = null
    override var receiptId: String? = null
    override var fromTransaction: Boolean = false

    private lateinit var currentIpu: RealmIpu

    override fun onViewAttached() {
        if (receiptId == null) {
            currentIpu = RealmIpu(
                    receipt = RealmReceipt(
                            barcode = receipt!!.barcode,
                            name = receipt!!.name,
                            address = receipt!!.address,
                            amount = receipt!!.amount
                    )
            )
            fetchIpus(receipt!!.barcode)
        } else {
            getRealmIpu(receiptId!!)
        }
    }

    override fun onSendValuesClick() {
        if (!validateData()) {
            return
        }

        val values = ArrayMap<String, String>()
        currentIpu.ipuValues.filter { !it.isSent }.forEach {
            values.put(it.id, it.value)
        }

        if (activeSession.isOfflineSession) {
            saveValuesToTransactions(values)
        } else {
            sendValues(values)
        }
    }

    private fun fetchIpus(code: String) {
        view?.setProgressVisibility(true)
        if (!fromTransaction) {
            subscriptions += ipuRepository.getByReceipt(code)
                    .subscribe(
                            {
                                ipuData ->
                                ipuData.forEach {
                                    currentIpu.ipuValues.add(
                                            RealmIpuValue(
                                                    id = it.id,
                                                    serviceName = it.serviceName,
                                                    shortName = it.shortName,
                                                    number = it.number,
                                                    installPlace = it.installPlace,
                                                    period = it.period
                                            )
                                    )
                                }
                                view?.showIpu(currentIpu)
                                view?.setProgressVisibility(false)
                            },
                            {
                                error ->
                                error.printStackTrace()
                                view?.close()
                                view?.showMessage(error.parsedMessage())
                            }
                    )
        } else {
            subscriptions += Observable.zip(
                    realmRepository.fetchOfflineIpuByReceiptId(receiptId!!),
                    ipuRepository.getByReceipt(code),
                    ::IpuValueAndIpu)
                    .subscribe(
                            {
                                ipuData ->

                                ipuData.ipus.forEach {
                                    ipu ->
                                    currentIpu.ipuValues.add(
                                            RealmIpuValue(
                                                    id = ipu.id,
                                                    serviceName = ipu.serviceName,
                                                    shortName = ipu.shortName,
                                                    number = ipu.number,
                                                    installPlace = ipu.installPlace,
                                                    period = ipuData.offlineIpu.createDate,
                                                    value = ipuData.offlineIpu.values.first { it.ipuId == ipu.id }.value
                                            )
                                    )
                                }
                                view?.showIpu(currentIpu)
                                view?.setProgressVisibility(false)
                            },
                            {
                                error ->
                                error.printStackTrace()
                                view?.close()
                                view?.showMessage(error.parsedMessage())
                            }
                    )
        }
    }

    private fun sendValues(values: ArrayMap<String, String>) {
        view?.setProgressVisibility(true)
        subscriptions += ipuRepository.sendParameters(currentIpu.receipt!!.barcode, values)
                .concatMap {
                    response ->
                    val now = Calendar.getInstance().time
                    currentIpu.ipuValues.filter { !it.isSent }.forEach {
                        it.isSent = true
                        it.date = now
                    }
                    if (activeSession.accessToken != null) {
                        realmRepository.saveIpu(currentIpu)
                    } else {
                        Observable.just(null)
                    }
                }
                .subscribe(
                        {
                            view?.showInfoDialog(R.string.ok_ipu_sended)
                            view?.setProgressVisibility(false)
                            view?.close()
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun saveValuesToTransactions(values: ArrayMap<String, String>) {
        view?.setProgressVisibility(true)

        val offlineIpus = RealmOfflineIpu(
                createDate = Calendar.getInstance().time,
                receipt = currentIpu.receipt!!)

        offlineIpus.values.addAll(values.map { RealmOfflineIpuValue(it.key, it.value) })

        subscriptions += realmRepository.saveOfflineIpu(offlineIpus)
                .subscribe(
                        {
                            view?.setProgressVisibility(false)
                            view?.showMessage(R.string.transaction_save_to_transaction_help_text)
                            view?.close()
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun getRealmIpu(receiptId: String) {
        subscriptions += realmRepository.fetchIpuByReceiptId(receiptId)
                .subscribe(
                        {
                            realmIpu ->
                            currentIpu = realmIpu

                            if (activeSession.isOfflineSession) {

                                if (currentIpu.ipuValues.isEmpty()) {
                                    view?.showInfoDialog(R.string.send_values_no_cached_ipu_error)
                                    return@subscribe
                                }

                                val ipus = currentIpu.ipuValues.distinctBy { it.number }
                                val now = Calendar.getInstance().time

                                ipus.forEach {
                                    currentIpu.ipuValues.add(
                                            RealmIpuValue(
                                                    it.id,
                                                    it.serviceName,
                                                    it.shortName,
                                                    it.number,
                                                    it.installPlace,
                                                    null,
                                                    now,
                                                    false,
                                                    ""
                                            )
                                    )
                                }

                                view?.showIpu(currentIpu)
                                view?.setProgressVisibility(false)

                            } else {
                                fetchIpus(currentIpu.receipt!!.barcode)
                            }
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun validateData(): Boolean {
        currentIpu.ipuValues.filter { !it.isSent }.forEach {
            if (it.value.isNullOrBlank()) {
                view?.showMessage(R.string.error_all_fields_required)
                return false
            }
        }
        return true
    }
}
