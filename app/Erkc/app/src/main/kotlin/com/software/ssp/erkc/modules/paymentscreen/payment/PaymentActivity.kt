package com.software.ssp.erkc.modules.paymentscreen.payment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.di.AppComponent
import org.jetbrains.anko.progressDialog
import javax.inject.Inject

/**
 * @author Alexander Popov on 10/11/2016.
 */
class PaymentActivity : MvpActivity(), IPaymentView {

    @Inject lateinit var presenter: IPaymentPresenter
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        initViews()
        presenter.onViewAttached()
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


    override fun setLoadingVisible(isVisible: Boolean) {
        if (progressDialog == null) {
            progressDialog = progressDialog(R.string.data_loading)
            progressDialog?.setCanceledOnTouchOutside(false)
        }
        if (isVisible) progressDialog?.show() else progressDialog?.dismiss()
    }

    override fun showActivatedCards(cards: List<Card>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToDrawer() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun navigateToResult() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showConfirmDialog() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNotificationsDialog() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerPaymentComponent.builder()
                .appComponent(appComponent)
                .paymentModule(PaymentModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    private fun initViews() {

    }

}