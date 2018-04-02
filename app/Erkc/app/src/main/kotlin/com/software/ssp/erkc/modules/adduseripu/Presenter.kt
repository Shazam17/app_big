package com.software.ssp.erkc.modules.adduseripu

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
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class Presenter @Inject constructor(view: IModuleView) : RxPresenter<IModuleView>(view), IModulePresenter {

    @Inject lateinit var ipuRepository: IpuRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository

    override var receipt: Receipt? = null
    override var receiptId: String? = null

    class UserIPUData {
        var number: String = ""
        var location: String = ""
    }

    val ipu_data = UserIPUData()

    override fun onViewAttached() {
        view?.bindData(ipu_data)
    }


    override fun commitClicked() {
        Timber.d("commit: number==${ipu_data.number} ; location==${ipu_data.location}")
    }
}
