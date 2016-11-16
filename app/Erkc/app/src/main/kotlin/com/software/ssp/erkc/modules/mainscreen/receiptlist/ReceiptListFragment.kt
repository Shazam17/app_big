package com.software.ssp.erkc.modules.mainscreen.receiptlist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.common.receipt.ReceiptSectionViewModel
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import com.software.ssp.erkc.modules.paymentscreen.payment.PaymentActivity
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject

class ReceiptListFragment : BaseListFragment<ReceiptSectionViewModel, IReceiptListView, IReceiptListPresenter>(), IReceiptListView {

    @Inject lateinit var presenter: IReceiptListPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerReceiptListScreenComponent.builder()
                .appComponent(appComponent)
                .receiptListScreenModule(ReceiptListScreenModule(this))
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
        val adapter = ReceiptListAdapter(dataset,
                { receipt -> presenter.onPayButtonClick(receipt) },
                { receipt -> presenter.onTransferButtonClick(receipt) },
                { receipt -> presenter.onHistoryButtonClick(receipt) },
                { receipt, position ->
                    dataset.find { it.address == receipt.address }?.receipts?.remove(receipt)
                    adapter?.notifyDataSetChanged()
                    presenter.onReceiptDeleted(receipt)
                })

        adapter.shouldShowHeadersForEmptySections(false)

        return adapter
    }

    override fun navigateToAddReceiptScreen() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewReceiptFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun navigateToIPUInputScreen(receipt: Receipt) {
        //TODO: NavigateToEnterValues
        showMessage("TODO: NavigateToSendValues - " + receipt.barcode)
    }

    override fun navigateToPayScreen(receipt: Receipt) {
        startActivityForResult<PaymentActivity>(Constants.REQUEST_CODE_PAYMENT, Constants.KEY_RECEIPT to receipt)
    }

    override fun navigateToHistoryScreen(receipt: Receipt) {
        //TODO: NavigateToPayment
        showMessage("TODO: NavigateToHistory - " + receipt.barcode)
    }
}
