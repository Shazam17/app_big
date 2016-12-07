package com.software.ssp.erkc.modules.paymentcheck

import android.net.Uri
import android.util.Base64
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ErkcFileManager
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.PaymentCheckFile
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject

/**
 * @author Alexander Popov on 30/11/2016.
 */
class PaymentCheckPresenter @Inject constructor(view: IPaymentCheckView) : RxPresenter<IPaymentCheckView>(view), IPaymentCheckPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var paymentRepository: PaymentRepository
    @Inject lateinit var realmRepo: RealmRepository
    @Inject lateinit var fileManager: ErkcFileManager
    private var uri: Uri? = null

    override fun onViewAttached(id: String) {
        view?.setLoadingVisible(true)
        subscriptions += Observable.zip(paymentRepository.fetchCheck(id), realmRepo.fetchPaymentById(id),
                {
                    check, payment ->
                    PaymentCheckFile(payment.checkFile, check.fileCheck)
                })
                .subscribe({
                    response ->
                    uri = fileManager.createTempFile(Base64.decode(response.data, Base64.DEFAULT), response.name)
                    view?.showCheck(uri!!)
                    view?.setLoadingVisible(false)
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onDownloadClick(paymentId: String) {
        if (uri != null) {
            fileManager.saveFile(uri!!)
        } else {
            view?.showMessage(R.string.payment_check_error)
        }
    }

    override fun onShareClick() {
        if (uri != null) {
            view?.showShareDialog(uri!!)
        } else {
            view?.showMessage(R.string.payment_check_error)
        }
    }
}