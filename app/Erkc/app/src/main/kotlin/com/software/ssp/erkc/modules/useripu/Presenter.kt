package com.software.ssp.erkc.modules.useripu

import android.util.ArrayMap
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.*
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import io.realm.RealmList
import rx.Observable
import rx.functions.Func2
import rx.lang.kotlin.plusAssign
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class Presenter @Inject constructor(view: IModuleView) : RxPresenter<IModuleView>(view), IModulePresenter {

    @Inject lateinit var ipuRepository: IpuRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override var ipu_number: String? = null
    override var receiptId: String? = null

    class UserIPUData (
            var number: String = "",
            var location: String = ""
    )

    var ipu_data = UserIPUData()
        set(value) {
            view?.bindData(value)
            value
        }

    fun editMode() = ipu_number != null

    override fun onViewAttached() {
        view?.bindData(ipu_data)
        if (editMode()) {
            view?.setModeEdit()
            fetchIPUData()
        } else {
            view?.setModeAdd()
        }
    }

    private fun fetchIPUData() {
        subscriptions += realmRepository.fetchIpuByReceiptId(receiptId!!)
                .concatMap {
                    ipus ->
                    val ipu = ipus.ipuValues.find { it.number == ipu_number }
                    Observable.just(UserIPUData(
                            number = ipu?.number ?: "",
                            location = ipu?.installPlace ?: ""
                    ))
                }
                .subscribe(
                        {
                            ipu_data = it
                            view?.setData(it)
                        }
                )
    }

    override fun commitClicked() {
        Timber.d("commit: number==${ipu_data.number} ; location==${ipu_data.location}")
        saveData() //TODO: validate
    }

    private fun saveData() {
        subscriptions += realmRepository.fetchIpuByReceiptId(receiptId!!)
                .concatMap {
                    ipu ->
                    if (editMode()) {
                        ipu.ipuValues.filter { it.number == ipu_number }.forEach {
                            it.number = ipu_data.number
                            it.installPlace = ipu_data.location
                        }
                    } else {
                        ipu.ipuValues.add(RealmIpuValue(
                                number = ipu_data.number,
                                installPlace = ipu_data.location,
                                isSent = true,
                                userRegistered = true
                        ))
                    }
                    Observable.just(ipu)
                }
                .concatMap {
                    realmRepository.saveIpu(it)
                }
                .subscribe(
                        {
                            if (!activeSession.isOfflineSession) {
                                //TODO: send to server
                            }
                        },
                        {
                            error->view?.showMessage(error.parsedMessage())
                        },
                        {
                            view?.close()
                        }
                )
    }

    override fun delete() {
        subscriptions += realmRepository.fetchIpuByReceiptId(receiptId!!)
                .zipWith(view?.askDeleteIPUAndData()!!, {ipu, result -> Pair(ipu, result)})
                .subscribe(
                        {
                            if (it.second) {
                                val ipu = it.first
                                ipu.ipuValues.removeAll(ipu.ipuValues.filter { it.number == ipu_number })
                                subscriptions += realmRepository.saveIpu(ipu).subscribe({ view?.close() })
                            }
                        }
                )
    }
}
