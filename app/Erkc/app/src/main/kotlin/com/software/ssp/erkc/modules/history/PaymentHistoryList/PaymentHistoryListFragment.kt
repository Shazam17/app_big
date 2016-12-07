package com.software.ssp.erkc.modules.history.PaymentHistoryList

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmPayment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.history.IHistoryListDelegate
import com.software.ssp.erkc.modules.paymentsinfo.PaymentInfoActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject


class PaymentHistoryListFragment : BaseListFragment<RealmPayment>(), IPaymentHistoryListView, IHistoryListDelegate {

    @Inject lateinit var presenter: IPaymentHistoryListPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerPaymentHistoryListComponent.builder()
                .appComponent(appComponent)
                .paymentHistoryListModule(PaymentHistoryListModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return PaymentHistoryListAdapter(
                dataset,
                { payment -> presenter.onPaymentClick(payment) }
        )
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun navigateToPaymentInfo(payment: RealmPayment) {
        startActivity<PaymentInfoActivity>(Constants.KEY_PAYMENT to payment.id)
    }

    override fun navigateToFilter() {
        showMessage("TODO Filter by Payment")
        //TODO startActivityForResult
    }

    override fun initViews() {
        super.initViews()
        emptyMessageText = ""
    }
}
