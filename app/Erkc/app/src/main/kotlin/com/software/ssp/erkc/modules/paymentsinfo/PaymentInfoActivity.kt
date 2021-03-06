package com.software.ssp.erkc.modules.paymentsinfo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo
import com.software.ssp.erkc.data.rest.models.PaymentStatus
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.extensions.type
import com.software.ssp.erkc.modules.paymentcheck.PaymentCheckActivity
import com.software.ssp.erkc.modules.paymentscreen.payment.PaymentActivity
import kotlinx.android.synthetic.main.activity_payment_info.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/12/2016.
 */
class PaymentInfoActivity : MvpActivity(), IPaymentInfoView {

    @Inject lateinit var presenter: IPaymentInfoPresenter

    private var paymentId: String by extras(Constants.KEY_PAYMENT)
    private var isHasCheck = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_info)

        presenter.paymentId = paymentId

        initViews()

        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPaymentInfoComponent.builder()
                .appComponent(appComponent)
                .paymentInfoModule(PaymentInfoModule(this))
                .build()
                .inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun close() {
        finish()
    }

    override fun fillData(paymentInfo: RealmPaymentInfo) {
        if (paymentInfo.status == PaymentStatus.SUCCESS.ordinal) {
            paymentInfoStatusErrorDescr.visibility = View.GONE
            paymentInfoStatusWrapper.setBackgroundResource(R.drawable.payment_info_successfully)
            paymentInfoStatusTitle.setText(R.string.payment_info_status_success)
        } else {
            paymentInfoStatusErrorDescr.visibility = View.VISIBLE
            paymentInfoStatusErrorDescr.text = paymentInfo.errorDesc
            paymentInfoStatusWrapper.setBackgroundResource(R.drawable.payment_info_error)
            paymentInfoStatusTitle.setText(R.string.payment_info_status_error)
        }
        paymentInfoBarcode.text = "${paymentInfo.barcode} (${paymentInfo.receipt?.name})"
        paymentInfoAddress.text = paymentInfo.address
        paymentInfoStatusDateAndTime.text = paymentInfo.date?.toString(Constants.DATE_TIME_FORMAT_PAYMENTS_UI)

        val paymentWithoutCommission = paymentInfo.sum * 100 / (paymentInfo.receipt!!.percent + 100)
        paymentInfoSum.text = getString(R.string.payment_info_currency).format(paymentWithoutCommission)
        paymentInfoCommission.text = getString(R.string.payment_info_commission_format).format(paymentInfo.sum - paymentWithoutCommission, paymentInfo.receipt?.percent)
        paymentInfoResult.text = getString(R.string.payment_info_currency).format(paymentInfo.sum)

        paymentInfoOperationNo.text = paymentInfo.operationId
        paymentInfoPaymentType.setText(paymentInfo.type())

        isHasCheck = !paymentInfo.checkFile.isNullOrBlank()
        paymentInfoShowCheckButton.enabled = isHasCheck
    }

    override fun setProgressVisibility(isVisible: Boolean) {
        paymentInfoRetryButton.enabled = !isVisible
        paymentInfoShowCheckButton.enabled = isHasCheck && !isVisible
        paymentInfoProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun navigateToCheck(payment: RealmPaymentInfo) {
        startActivity<PaymentCheckActivity>("paymentId" to payment.id, "checkName" to payment.checkFile!!)
    }

    override fun navigateToRetryPayment(payment: RealmPaymentInfo) {
        startActivity<PaymentActivity>("paymentId" to payment.id)
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)

        paymentInfoRetryButton.onClick {
            presenter.onRetryClick()
        }

        paymentInfoShowCheckButton.onClick {
            presenter.onGetCheckClick()
        }
    }
}
