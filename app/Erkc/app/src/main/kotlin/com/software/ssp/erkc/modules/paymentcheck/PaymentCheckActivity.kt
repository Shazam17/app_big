package com.software.ssp.erkc.modules.paymentcheck

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.extras
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_payment_check.*
import javax.inject.Inject


/**
 * @author Alexander Popov on 30/11/2016.
 */
class PaymentCheckActivity : MvpActivity(), IPaymentCheckView {
    @Inject lateinit var presenter: IPaymentCheckPresenter

    val checkName: String? by extras()
    val paymentId: String by extras()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_check)
        initViews()

        presenter.checkUrl = checkName
        presenter.paymentId = paymentId

        presenter.onViewAttached()
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

    override fun showCheck(uri: Uri) {
        paymentPdfView.fromUri(uri).load()
    }

    override fun showShareDialog(uri: Uri) {
        val intent = ShareCompat.IntentBuilder.from(this)
                .setStream(uri)
                .setType("application/pdf")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
            R.id.payment_check_menu_download -> presenter.onDownloadClick()
            R.id.payment_check_menu_share -> presenter.onShareClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
    }

}