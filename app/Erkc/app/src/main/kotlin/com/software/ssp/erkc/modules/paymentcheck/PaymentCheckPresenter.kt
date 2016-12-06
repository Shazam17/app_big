package com.software.ssp.erkc.modules.paymentcheck

import android.util.Base64
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.ErkcFileManager
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.models.PaymentCheck
import com.software.ssp.erkc.data.rest.repositories.PaymentRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * @author Alexander Popov on 30/11/2016.
 */
class PaymentCheckPresenter @Inject constructor(view: IPaymentCheckView) : RxPresenter<IPaymentCheckView>(view), IPaymentCheckPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var paymentRepository: PaymentRepository
    @Inject lateinit var fileManager: ErkcFileManager

    override fun onViewAttached(id: String) {
        view?.setLoadingVisible(true)
        subscriptions += paymentRepository.fetchCheck(id)
                .subscribe({
                    response ->
                    val pdfFile = savePdfToTempStorage(response)
                    view?.showCheck(pdfFile)
                    view?.setLoadingVisible(false)
                }, {
                    error ->
                    view?.setLoadingVisible(false)
                    view?.showMessage(error.parsedMessage())
                })
    }

    override fun onDownloadClick(file: File?, fileName: String) {
        if (file != null) {
            fileManager.saveFile(fileName, file)
        } else {
            view?.showMessage(R.string.payment_check_error)
        }
    }

    override fun onShareClick(file: File?) {
        if (file != null) {
            view?.showShareDialog(file)
        } else {
            view?.showMessage(R.string.payment_check_error)
        }
    }

    private fun savePdfToTempStorage(check: PaymentCheck): File {
        val tempPdfFile = createTempFile()
        val outputStream = FileOutputStream(tempPdfFile)
        outputStream.write(Base64.decode(check.fileCheck, Base64.DEFAULT))
        outputStream.close()
        return tempPdfFile
    }
}