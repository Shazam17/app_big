package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import javax.inject.Inject

class PaymentListFragment : BaseListFragment<ReceiptSectionViewModel, IPaymentListView, IPaymentListPresenter>(), IPaymentListView {

    @Inject lateinit var presenter: IPaymentListPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerPaymentListScreenComponent.builder()
                .appComponent(appComponent)
                .paymentListScreenModule(PaymentListScreenModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.value_transfer_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_receipt_add -> {
                presenter.onAddReceiptButtonClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        val adapter = PaymentListAdapter(dataset,
                { receipt -> presenter.onPayButtonClick(receipt) },
                { receipt, position ->
                    dataset.find { it.address == receipt.address }?.receipts?.remove(receipt)
                    adapter?.notifyDataSetChanged()
                    presenter.onReceiptDeleted(receipt)
                })

        adapter.shouldShowHeadersForEmptySections(false)

        return adapter
    }

    override fun navigateToAddReceiptScreen() {
        //TODO: NavigateToAdd
        showMessage("TODO: NavigateToAdd")
    }

    override fun navigateToPayScreen(receipt: Receipt) {
        //TODO: NavigateToPayment
        showMessage("TODO: NavigateToPayment - " + receipt.barcode)
    }
}
