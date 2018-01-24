package com.software.ssp.erkc.modules.transaction.paymenttransaction

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmOfflinePayment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.paymentscreen.payment.PaymentActivity
import kotlinx.android.synthetic.main.fragment_transaction_payments_list.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 13/12/2016.
 */
class PaymentTransactionListFragment : BaseListFragment<RealmOfflinePayment>(), IPaymentTransactionListView {

    @Inject lateinit var presenter: IPaymentTransactionListPresenter

    override fun navigateToPaymentInfo(payment: RealmOfflinePayment) {
        startActivity<PaymentActivity>(Constants.KEY_FROM_TRANSACTION to true, Constants.KEY_RECEIPT to payment.receipt.id)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_transaction_payments_list, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerPaymentTransactionListComponent.builder()
                .appComponent(appComponent)
                .paymentTransactionListModule(PaymentTransactionListModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onSwipeToRefresh() {

    }

    override fun onResume() {
        super.onResume()

        presenter.showPaymentsList()
    }

    override fun showAlert(message: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
            .setNeutralButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
            })
        builder.create().show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return PaymentTransactionListAdapter(dataset, object : PaymentTransactionListAdapter.InteractionListener {
            override fun onPayClick(payment: RealmOfflinePayment) {
                presenter.onPaymentClick(payment)
            }

            override fun deleteClick(payment: RealmOfflinePayment) {
                presenter.onDeleteClick(payment)
            }

        })
    }

    override fun initViews() {
        super.initViews()
        swipeToRefreshEnabled = false
    }

    override fun setEmptyViewVisible(visible: Boolean) {
        emptyView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

}