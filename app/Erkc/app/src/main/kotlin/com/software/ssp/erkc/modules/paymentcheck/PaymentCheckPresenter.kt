package com.software.ssp.erkc.modules.paymentcheck

import android.net.Uri
import android.util.Base64
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ErkcFileManager
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 30/11/2016.
 */
class PaymentCheckPresenter @Inject constructor(view: IPaymentCheckView) : RxPresenter<IPaymentCheckView>(view), IPaymentCheckPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var paymentRepository: PaymentRepository
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var fileManager: ErkcFileManager

    override var checkUrl: String? = null
    override var paymentId: String = ""

    private var fileUri: Uri? = null

    override fun onViewAttached() {
        fetchCheckFile()
    }

    override fun onDownloadClick() {
        if (fileUri != null) {
            fileManager.saveFile(fileUri!!)
        } else {
            view?.showMessage(R.string.payment_check_error)
        }
    }

    override fun onShareClick() {
        if (fileUri != null) {
            view?.showShareDialog(fileUri!!)
        } else {
            view?.showMessage(R.string.payment_check_error)
        }
    }

    private fun fetchCheckFile() {
        view?.setLoadingVisible(true)
        subscriptions += paymentRepository.fetchCheck(paymentId)
                .subscribe(
                        {
                            check ->
                            fileUri = fileManager.createTempFile(Base64.decode(check.fileCheck, Base64.DEFAULT), checkUrl!!)
                            view?.showCheck(fileUri!!)
                            view?.setLoadingVisible(false)
                        },
                        {
                            error ->
                            view?.setLoadingVisible(false)
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}