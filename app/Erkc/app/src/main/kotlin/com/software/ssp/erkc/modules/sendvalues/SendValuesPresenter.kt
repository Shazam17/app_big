package com.software.ssp.erkc.modules.sendvalues

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmIpu
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.realm.models.RealmReceipt
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

    @Inject lateinit var ipuRepository: IpuRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override var receipt: Receipt? = null
    override var receiptId: String? = null

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
        if(!validateData()){
            return
        }

        val values = HashMap<String, String>()
        currentIpu.ipuValues.filter { !it.isSent }.forEach {
            values.put("ipu_" + it.id, it.value)
        }

        view?.setProgressVisibility(true)
        subscriptions += ipuRepository.sendParameters(currentIpu.receipt!!.barcode, values)
                .concatMap {
                    response ->
                    val now = Calendar.getInstance().time
                    currentIpu.ipuValues.filter { !it.isSent }.forEach {
                        it.isSent = true
                        it.date = now
                    }
                    realmRepository.saveIpu(currentIpu)
                }
                .subscribe(
                        {
                            view?.setProgressVisibility(false)
                        },
                        {
                            error ->
                            view?.setProgressVisibility(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun fetchIpus(code: String) {
        view?.setProgressVisibility(true)
        subscriptions += ipuRepository.getByReceipt(code)
                .subscribe(
                        {
                            ipuData ->
                            ipuData.forEach {
                                currentIpu.ipuValues.add(
                                        0,
                                        RealmIpuValue(
                                                id = it.id,
                                                serviceName = it.serviceName,
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
                            fetchIpus(currentIpu.receipt!!.barcode)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )

    }

    private fun validateData() : Boolean {
        currentIpu.ipuValues.filter { !it.isSent }.forEach {
            if(it.value.isNullOrBlank()) {
                view?.showMessage(R.string.error_all_fields_required)
                return false
            }
        }
        return true
    }
}
