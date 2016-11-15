package com.software.ssp.erkc.modules.autopayments

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.ReceiptsRepository
import javax.inject.Inject


class AutoPaymentsTabPresenter @Inject constructor(view: IAutoPaymentsTabView) : RxPresenter<IAutoPaymentsTabView>(view), IAutoPaymentsTabPresenter {

    @Inject lateinit var receiptsRepository: ReceiptsRepository
    @Inject lateinit var activeSession: ActiveSession

}
