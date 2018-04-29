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
import com.software.ssp.erkc.modules.useripu.IModulePresenter.FilterType
import com.software.ssp.erkc.modules.useripu.IModulePresenter.FilterType.*
import kotlinx.android.synthetic.main.activity_add_user_ipu.view.*
import kotlin.collections.ArrayList

class Presenter @Inject constructor(view: IModuleView) : RxPresenter<IModuleView>(view), IModulePresenter {

    @Inject lateinit var ipuRepository: IpuRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override var ipu_number: String? = null
    override var receiptId: String? = null

    private var barcode: String? = null

    class UserIPUData (
            var id: String = "", //from backend after saving
            var number: String = "",
            var location: String = "",
            var service_name: String = "",
            var brand: String = "",
            var model: String = "",
            var check_interval: String = "",
            var type: String = "",
            var type_tariff: String = "",
            var begin_date: String = "",
            var install_date: String = "",
            var next_check_date: String = "",
            var status: String = ""

    ) {
        var realm: RealmRepository? = null

        fun locationId() = idFor(location, realm?.fetchIpuLocations())
        fun serviceNameId() = idFor(service_name, realm?.fetchIpuServiceNames())
        fun checkIntervalId() = idFor(check_interval, realm?.fetchIpuCheckIntervals())
        fun typeId() = idFor(type, realm?.fetchIpuTypes())
        fun typeTariffId() = idFor(type_tariff, realm?.fetchIpuTypesTariff())

        fun checkIntervalFromId(id: String?) = fromId(id, realm?.fetchIpuCheckIntervals())
        fun typeFromId(id: String?) = fromId(id, realm?.fetchIpuTypes())
        fun typeTariffFromId(id: String?) = fromId(id, realm?.fetchIpuTypesTariff())

        private fun fromId(id: String?, list: List<RealmIdName>?): String {
            if (id == null || list == null) return ""
            return list.find { it.id.equals(id) }?.name ?: ""
        }

        private fun idFor(what: String, list: RealmList<RealmIdName>?): String {
            return if (list != null)
                list.find { it.name.equals(what) }?.id ?: "-10" //Error
            else
                "-11" //Error
        }

    }

    var ipu_data = UserIPUData()
        set(value) {
            view?.bindData(value)
            value.realm = realmRepository
            value
        }

    fun editMode() = ipu_number != null

    override fun onViewAttached() {
        ipu_data.realm = realmRepository

        view?.bindData(ipu_data)
        view?.setupFilters() //after bindData()

        getBarcode()

        if (editMode()) {
            view?.setModeEdit()
            fetchIPUData()
        } else {
            view?.setModeAdd()
        }
    }

    private fun getBarcode() {
        realmRepository.fetchIpuByReceiptId(receiptId!!)
                .subscribe({ realmIpu -> barcode = realmIpu?.receipt?.barcode })
    }

    private fun fetchIPUData() {
        subscriptions += realmRepository.fetchIpuByReceiptId(receiptId!!)
                .concatMap {
                    ipus ->
                    val ipu = ipus.ipuValues.find { it.number == ipu_number }
                    Observable.just(UserIPUData(
                            id = ipu?.id ?: "",
                            number = ipu?.number ?: "",
                            location = ipu?.installPlace ?: "",
                            service_name = ipu?.shortName ?: "",
                            brand = ipu?.brand ?: "",
                            model = ipu?.model ?: "",
                            check_interval = ipu?.check_interval ?: "",
                            type = ipu?.type ?: "",
                            type_tariff = ipu?.type_tariff ?: "",
                            begin_date = ipu?.begin_date ?: "",
                            install_date = ipu?.install_date ?: "",
                            next_check_date = ipu?.next_check_date ?: "",
                            status = ipu?.status ?: ""
                    ))
                }
                .subscribe(
                        {
                            ipu_data = it
                            ipu_data.id = it.id
                            view?.setData(it)
                        }
                )
    }

    override fun commitClicked() {
        Timber.d("commit: number==${ipu_data.number} ; location==${ipu_data.location}")
        if (view?.validateDataBeforeCommit()!!) saveData()
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
                                serviceName = ipu_data.service_name,
                                brand = ipu_data.brand,
                                model = ipu_data.model,
                                check_interval = ipu_data.check_interval,
                                type = ipu_data.type,
                                type_tariff = ipu_data.type_tariff,
                                begin_date = ipu_data.begin_date,
                                install_date = ipu_data.install_date,
                                next_check_date = ipu_data.next_check_date,
                                status = ipu_data.status,

                                isSent = true,
                                userRegistered = true
                        ))
                    }
                    Observable.just(ipu)
                }
                .concatMap {
                    realmRepository.saveIpu(it)
                }
                .concatMap {
                    if (!activeSession.isOfflineSession) {
                        if (editMode())
                            ipuRepository.updateByUser(barcode!!, ipu_data)
                        else
                            ipuRepository.addByUser(barcode!!, ipu_data)
                    } else
                        Observable.just(null)
                }
                .doOnSubscribe { view?.showProgress(true) }
                .doOnError { view?.showProgress(false) }
                .subscribe(
                        { response ->
                            Timber.d("save response: $response")
                            view?.close()
                        },
                        { error->view?.showMessage(error.parsedMessage()) }
                )
    }

    override fun delete() {
        subscriptions += realmRepository.fetchIpuByReceiptId(receiptId!!)
                .zipWith(view?.askDeleteIPUAndData()!!, {ipu, result -> Pair(ipu, result)})
                .subscribe(
                        {
                            if (it.second) {
                                val cleaned_ipu = it.first
                                cleaned_ipu.ipuValues.removeAll(cleaned_ipu.ipuValues.filter { it.number == ipu_number })
                                subscriptions += ipuRepository.removeByUser(barcode!!, ipu_data)
                                        .concatMap {
                                            realmRepository.saveIpu(cleaned_ipu)
                                        }
                                        .subscribe(
                                            { view?.close() },
                                            { error -> view?.showMessage(error.message.toString()) }
                                        )


                            }
                        }
                )
    }

    override fun filter(type: FilterType): List<String> {
        return when (type) {
            FILTER_LOCATION -> realmRepository.fetchIpuLocations().map { it.name }
            FILTER_SERVICE_NAME -> realmRepository.fetchIpuServiceNames().map { it.name }
            FILTER_CHECK_INTERVAL -> realmRepository.fetchIpuCheckIntervals().map { it.name }
            FILTER_TYPE -> realmRepository.fetchIpuTypes().map { it.name }
            FILTER_TYPE_TARIFF -> realmRepository.fetchIpuTypesTariff().map { it.name }
            FILTER_STATUS -> realmRepository.fetchIpuStatuses().map { it.name }
            FILTER_CLOSE_REASON -> realmRepository.fetchIpuCloseReasons().map { it.name }
        }
    }
}
