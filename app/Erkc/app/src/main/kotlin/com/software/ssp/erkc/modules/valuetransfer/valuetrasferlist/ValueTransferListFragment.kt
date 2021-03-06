package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import com.software.ssp.erkc.modules.sendvalues.SendValuesActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.withArguments
import javax.inject.Inject


class ValueTransferListFragment : BaseListFragment<ReceiptViewModel>(), IValueTransferListView {

    @Inject lateinit var presenter: IValueTransferListPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerValueTransferListComponent.builder()
                .appComponent(appComponent)
                .valueTransferListModule(ValueTransferListModule(this))
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
        inflater?.inflate(R.menu.receipts_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_receipt_add -> {
                presenter.onAddNewValueTransferClick()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun receiptDidNotDeleted(receipt: RealmReceipt) {
        val receiptIndex = dataset.indexOfFirst { it.receipt == receipt }
        dataset[receiptIndex].isRemovePending = false
        adapter?.notifyItemChanged(receiptIndex)
    }

    override fun receiptDeleted(receipt: RealmReceipt) {
        val receiptIndex = dataset.indexOfFirst { it.receipt == receipt }
        dataset.removeAt(receiptIndex)
        adapter?.notifyItemRemoved(receiptIndex)
    }

    override fun navigateToSendValues(receipt: RealmReceipt) {
        startActivity<SendValuesActivity>(Constants.KEY_RECEIPT to receipt.id)
    }

    override fun navigateToAddReceiptScreen() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewReceiptFragment().withArguments("isTransferValue" to true, "isTransferValueVisible" to false))
                .addToBackStack(null)
                .commit()
    }

    override fun navigateToEmptyReceiptsList() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewReceiptFragment().withArguments("isTransferValue" to true, "isTransferValueVisible" to false))
                .commit()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValueTransferAdapter(dataset,
                object : ValueTransferAdapter.InteractionListener {
                    override fun transferClick(receipt: RealmReceipt) {
                        presenter.onTransferValueClick(receipt)
                    }

                    override fun deleteClick(receipt: RealmReceipt) {
                        presenter.onReceiptDeleted(receipt)
                    }
                })
    }
}
