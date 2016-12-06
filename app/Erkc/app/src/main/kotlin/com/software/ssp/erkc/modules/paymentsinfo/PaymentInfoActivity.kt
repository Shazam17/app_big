package com.software.ssp.erkc.modules.paymentsinfo

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.realm.models.RealmPayment
import com.software.ssp.erkc.data.realm.models.RealmPaymentInfo
import com.software.ssp.erkc.data.rest.models.PaymentStatus
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.type
import kotlinx.android.synthetic.main.activity_payment_info.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.onClick
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 01/12/2016.
 */
class PaymentInfoActivity : MvpActivity(), IPaymentInfoView {

    @Inject lateinit var presenter: IPaymentInfoPresenter
    private var paymentId: String by extras(Constants.KEY_PAYMENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_info)
        initViews()
        presenter.onViewAttached(paymentId)
    }

    override fun close() {
        finish()
    }

    override fun fillData(paymentInfo: RealmPaymentInfo, payment: RealmPayment) {
        if (paymentInfo.status == PaymentStatus.SUCCESS.ordinal) {
            paymentInfoStatusErrorDescr.visibility = View.GONE
            paymentInfoStatusWrapper.setBackgroundResource(R.drawable.payment_info_successfully)
            paymentInfoStatusTitle.setText(R.string.payment_info_status_success)
        } else {
            paymentInfoStatusErrorDescr.visibility = View.VISIBLE
            paymentInfoStatusErrorDescr.text = payment.errorDesc
            paymentInfoStatusWrapper.setBackgroundResource(R.drawable.payment_info_error)
            paymentInfoStatusTitle.setText(R.string.payment_info_status_error)
        }
        paymentInfoBarcode.text = "${paymentInfo.barcode} (${payment.receipt?.name})"
        paymentInfoAddress.text = paymentInfo.address
        paymentInfoStatusDateAndTime.text = SimpleDateFormat(Constants.DATE_TIME_FORMAT_PAYMENTS_UI, Locale("ru")).format(payment.date)
        paymentInfoSum.text = getString(R.string.payment_info_currency).format(paymentInfo.amount)
        paymentInfoResult.text = getString(R.string.payment_info_currency).format(paymentInfo.summ)
        paymentInfoCommission.text = getString(R.string.payment_info_currency).format(paymentInfo.summ - paymentInfo.amount)
        paymentInfoOperationNo.text = payment.operationId
        if (payment.methodId != null) {
            paymentInfoPaymentType.setText(payment.type()!!)
        } else {
            paymentInfoTypeWrapper.visibility = View.GONE
        }
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

    override fun setProgressVisibility(isVisible: Boolean) {
        paymentInfoRetryButton.enabled = !isVisible
        paymentInfoShowCheckButton.enabled = !isVisible
        paymentInfoProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPaymentInfoComponent.builder()
                .appComponent(appComponent)
                .paymentInfoModule(PaymentInfoModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
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