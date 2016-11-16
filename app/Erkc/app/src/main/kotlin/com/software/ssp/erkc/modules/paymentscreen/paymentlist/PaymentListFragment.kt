package com.software.ssp.erkc.modules.paymentscreen.paymentlist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import org.jetbrains.anko.withArguments
import javax.inject.Inject

class PaymentListFragment : BaseListFragment<Receipt, IPaymentListView, IPaymentListPresenter>(), IPaymentListView {

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
        inflater?.inflate(R.menu.receipts_list_menu, menu)
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

    override fun receiptDidNotDeleted(receipt: Receipt) {
        adapter?.notifyItemChanged(dataset.indexOf(receipt))
    }

    override fun receiptDeleted(receipt: Receipt){
        adapter?.notifyItemRemoved(dataset.indexOf(receipt))
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return PaymentListAdapter(dataset,
                { receipt -> presenter.onPayButtonClick(receipt) },
                { receipt, position -> presenter.onReceiptDeleted(receipt) })
    }

    override fun navigateToAddReceiptScreen() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewReceiptFragment().withArguments("isTransferValueVisible" to false))
                .addToBackStack(null)
                .commit()
    }

    override fun navigateToEmptyReceiptsList() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewReceiptFragment().withArguments("isTransferValueVisible" to false))
                .commit()
    }

    override fun navigateToPayScreen(receipt: Receipt) {
        //TODO: NavigateToPayment
        showMessage("TODO: NavigateToPayment - " + receipt.barcode)
    }
}
