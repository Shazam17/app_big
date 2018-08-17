package com.software.ssp.erkc.modules.history.valuehistory

import android.text.format.DateUtils
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.Constants.VALUES_DATE_FORMAT
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.IpuType
import com.software.ssp.erkc.data.realm.models.RealmIpuValue
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.IpuRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import com.software.ssp.erkc.extensions.getUnitResId
import com.software.ssp.erkc.extensions.getUnitResIdOnly
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import com.software.ssp.erkc.modules.useripu.Presenter
import rx.Observable
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.plusAssign
import rx.lang.kotlin.requireNoNulls
import rx.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author Alexander Popov on 05/12/2016.
 */
class ValueHistoryPresenter @Inject constructor(view: IValueHistoryView) : RxPresenter<IValueHistoryView>(view), IValueHistoryPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var ipuRepository: IpuRepository
    @Inject lateinit var receiptsRepository: ReceiptsRepository

    override var receiptId: String = ""
    override var currentFilter: HistoryFilterModel = HistoryFilterModel()

    private var date_from_cached = ""
    private var date_to_cached = ""

    var dismissProgressOnNextResume = false
    private var barcode = ""

    class ShareData (val address: String, val getString: (Int)->String) {
        private var data = ArrayList<String>()
        var account_num = ""
        private val ACCOUNT_PLACEHOLDER = "%ACCOUNT_PLACEHOLDER%"

        fun addIpu(ipus: List<RealmIpuValue>) {
            val recent = ipus.sortedBy { it.date }.last()
            val units = recent.ipuType.getUnitResIdOnly()
//            data.add("${recent.shortName} (${recent.number}, ${recent.installPlace}) ${recent.value} ${getString(units)}" +
//                    "\n${getString(R.string.history_share_last_data)}: ${recent.date?.toString(Constants.VALUES_DATE_FORMAT)}")
            data.add("$address\t" +
                    "${ACCOUNT_PLACEHOLDER}\t" +
                    "${recent.number}\t" +
                    "${recent.value}\t" +
                    "${recent.date?.toString(Constants.VALUES_DATE_FORMAT)}\t" +
                    "${recent.shortName}"
            )
        }

        class SheetData(val col: Int, val row: Int, val text: String)

        private fun sortData() {
            val sdf = SimpleDateFormat(VALUES_DATE_FORMAT)
            data.sortWith(
                    compareByDescending<String>({ //date
                        sdf.parse(it.split("\t").get(4)).time
                    })
                    .thenBy({ //service type in dictionary order
                        val service = it.split("\t").get(5)
                        val service_id = Presenter.UserIPUData(service_name = service).serviceNameId()
                        service_id
                    })
            )
        }

        fun sheetIterator(): ArrayList<SheetData> {
            val res = ArrayList<SheetData>()

            sortData()

            with(res) {
                getString(R.string.history_share_header).split("\t").forEachIndexed {index, s -> add(SheetData(index, 0, s)) }
                data.forEachIndexed { row, line ->
                    "${row+1}\t${line.replace(ACCOUNT_PLACEHOLDER, account_num)}".split("\t").forEachIndexed {col, cell -> add(SheetData(col, row+1, cell)) }
                }
                add(SheetData(0, data.size+1, "${getString(R.string.history_share_form_date)}: ${Date().toString(Constants.VALUES_DATE_FORMAT)}"))
            }

            return res
        }

        override fun toString(): String {
            //val sb = StringBuilder("${getString(R.string.history_share_address)}: $address")
            val sb = StringBuilder(getString(R.string.history_share_header))
            sb.append('\n')
            data.forEachIndexed { index, s -> sb.append("${index+1}\t${s.replace(ACCOUNT_PLACEHOLDER, account_num)}\n")}
            sb.append("${getString(R.string.history_share_form_date)}: ${Date().toString(Constants.VALUES_DATE_FORMAT)}")
            return sb.toString()
        }
    }
    var shareData: ShareData? = null

    override fun onViewAttached() {
        fetchData()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    private fun fetchData() {
        subscriptions += realmRepository.fetchReceiptsById(receiptId)
                .concatMap {
                    receipt ->

                    barcode = receipt!!.barcode

                    view?.showReceiptData(receipt)
                    shareData = ShareData(receipt.address, view!!::getString)

                    if (activeSession.isOfflineSession) {
                        Observable.just(null)
                    } else {
                        ipuRepository.getHistoryByReceipt(receipt!!.barcode)
                                .zipWith(ipuRepository.getByReceipt(receipt!!.barcode), ::Pair)
                    }
                }
                .concatMap {
                    pair ->
                    val ipus = pair?.first
                    val ipus_just = pair?.second

                    val map = HashMap<String, String>()
                    ipus_just?.forEach { map.put(it.id, it.user_registered?:"") }

                    ipus?.forEach{ it.user_registered = map.get(it.id) }

                    if (ipus == null) {
                        Observable.just(null)
                    } else {
                        realmRepository.saveIpusWithReceipt(ipus, receiptId)
                    }
                }
                .concatMap {
                    realmRepository.fetchIpuByReceiptId(receiptId)
                }
                .doOnSubscribe { view?.setProgressVisible(true) }
                .doOnTerminate { view?.setProgressVisible(false) }
                .subscribe(
                        {
                            ipu ->
                            val filteredIpuValues = ipu.ipuValues
                                    .filter {
                                        ipuValue ->

                                        when {
                                            !currentFilter.deviceNumber.isNullOrBlank() && ipuValue.number != currentFilter.deviceNumber -> return@filter false
                                            !currentFilter.deviceInstallPlace.isNullOrBlank() && ipuValue.installPlace != currentFilter.deviceInstallPlace -> return@filter false
                                            currentFilter.periodFrom != null && ipuValue.date != null && ipuValue.date!! < currentFilter.periodFrom -> return@filter false
                                            currentFilter.periodTo != null && ipuValue.date != null && ipuValue.date!! > currentFilter.periodTo -> return@filter false
                                        }

                                        return@filter true
                                    }
                                    .sortedByDescending { it.date }

                            fillData(filteredIpuValues)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun fillData(ipus: List<RealmIpuValue>) {
        val startCalendar = GregorianCalendar()
        val dateFrom = ipus.last().date
        startCalendar.time = dateFrom

        val endCalendar = GregorianCalendar()
        val dateTo = ipus.first().date
        endCalendar.time = dateTo

        val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
        val diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)

        date_from_cached = dateFrom!!.toString(Constants.VALUES_DATE_FORMAT)
        date_to_cached = dateTo!!.toString(Constants.VALUES_DATE_FORMAT)
        view?.showPeriod(date_from_cached, date_to_cached)

        ipus.groupBy { it.shortName }.forEach {

            var total = 0L

            it.value.groupBy { it.number }.forEach {
                val ipu = it.value

                val ipuTotal = if (ipu.count() > 1) ipu.first().value.toLong() - ipu.last().value.toLong() else ipu.first().value.toLong()
                val ipuAverage: Double = if (diffMonth <= 1) 0.0 else (1.0) * ipuTotal / diffMonth
                view?.addIpuData(ValueHistoryViewModel(ipu, ipuTotal, ipuAverage))

                total += ipuTotal

                shareData?.addIpu(it.value)
            }

            val average: Double = if (diffMonth <= 1) 0.0 else (1.0) * total / diffMonth
            val type = it.value.firstOrNull()?.ipuType ?: IpuType.UNKNOWN

            view?.addServiceData(it.key ?: "", type, total, average)
        }
    }

    override fun shareAction() {
        if (date_from_cached.isEmpty()) return //skip clicks, not ready yet

        view?.setProgressVisible(true)
        dismissProgressOnNextResume = true

        subscriptions += receiptsRepository.fetchReceiptInfo(barcode)
                .flatMap { receipt ->
                    shareData?.account_num = receipt.account
                    Observable.just(shareData)
                }
                .requireNoNulls()
                .subscribe(
                        {view?.shareIntent(it,
                                "xml/${it.address} (с ${date_from_cached} по ${date_to_cached}).xls".replace(File.pathSeparator, "-"),
                                "${it.address} (с ${date_from_cached} по ${date_to_cached})".replace(File.pathSeparator, "-"))},
                        {view?.shareDataNotReady()}
                )
    }

    override fun onResume() {
        if (dismissProgressOnNextResume) view?.setProgressVisible(false)
        dismissProgressOnNextResume = false
    }
}