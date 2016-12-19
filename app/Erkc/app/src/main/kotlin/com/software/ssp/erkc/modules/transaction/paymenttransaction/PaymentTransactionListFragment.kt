package com.software.ssp.erkc.modules.transaction.paymenttransaction

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmOfflinePayment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.paymentscreen.payment.PaymentActivity
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

}