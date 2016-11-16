package com.software.ssp.erkc.modules.mainscreen.receiptlist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import javax.inject.Inject

class ReceiptListFragment : BaseListFragment<Receipt, IReceiptListView, IReceiptListPresenter>(), IReceiptListView {

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

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun receiptDidNotDeleted(receipt: Receipt) {
        adapter?.notifyItemChanged(dataset.indexOf(receipt))
    }

    override fun receiptDeleted(receipt: Receipt) {
        val receiptIndex = dataset.indexOf(receipt)
        dataset.removeAt(receiptIndex)
        adapter?.notifyItemRemoved(receiptIndex)
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ReceiptListAdapter(dataset,
                { receipt -> presenter.onPayButtonClick(receipt) },
                { receipt -> presenter.onTransferButtonClick(receipt) },
                { menuItem, receipt ->
                    when (menuItem) {
                        ReceiptMenuItem.HISTORY -> presenter.onHistoryButtonClick(receipt)
                        ReceiptMenuItem.AUTOPAY -> presenter.onAutoPaymentButtonClick(receipt)
                    }
                },
                { receipt, position -> presenter.onReceiptDeleted(receipt) })
    }

    override fun navigateToAddReceiptScreen() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewReceiptFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun navigateToEmptyReceiptsList() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewReceiptFragment())
                .commit()
    }

    override fun navigateToIPUInputScreen(receipt: Receipt) {
        //TODO: NavigateToEnterValues
        showMessage("TODO: NavigateToSendValues - " + receipt.barcode)
    }

    override fun navigateToPayScreen(receipt: Receipt) {
        //TODO: NavigateToPayment
        showMessage("TODO: NavigateToPayment - " + receipt.barcode)
    }

    override fun navigateToHistoryScreen(receipt: Receipt) {
        //TODO: NavigateToHistory
        showMessage("TODO: NavigateToHistory - " + receipt.barcode)
    }

    override fun navigateToAutoPaymentSettingScreen(receipt: Receipt) {
        //TODO: NavigateToAutoPayment
        showMessage("TODO: NavigateToAutoPayment - " + receipt.barcode)
    }
}
