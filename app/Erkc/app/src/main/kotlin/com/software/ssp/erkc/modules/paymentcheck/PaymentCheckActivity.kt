package com.software.ssp.erkc.modules.paymentcheck

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Payment
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_payment_check.*
import java.io.File
import javax.inject.Inject


/**
 * @author Alexander Popov on 30/11/2016.
 */
class PaymentCheckActivity : MvpActivity(), IPaymentCheckView {
    @Inject lateinit var presenter: IPaymentCheckPresenter
    val payment: Payment by extras(Constants.KEY_PAYMENT)
    var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_check)
        initViews()
        presenter.onViewAttached(payment.id)
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPaymentCheckComponent.builder()
                .appComponent(appComponent)
                .paymentCheckModule(PaymentCheckModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun setLoadingVisible(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun showCheck(file: File) {
        this.file = file
        paymentPdfView.fromFile(file).load()
    }

    override fun showShareDialog(file: File) {
        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(file), "application/pdf")
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.payment_check_menu, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.payment_check_menu_download -> presenter.onDownloadClick(file, payment.checkFile)
            R.id.payment_check_menu_share -> presenter.onShareClick(file)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
    }

}