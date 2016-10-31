package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.valuetransfer.newvaluetransfer.NewValueTransferFragment
import javax.inject.Inject


class ValueTransferListFragment : BaseListFragment<ReceiptsViewModel, IValueTransferListView, IValueTransferListPresenter>(), IValueTransferListView {

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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.value_transfer_menu, menu)
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

    override fun navigateToSendValues(receipt: Receipt) {
        //TODO: NavigateToEnterValues
        showMessage("TODO: NavigateToSendValues - " + receipt.barcode)
    }

    override fun navigateToNewValueTransfer() {
        activity.fragmentManager.beginTransaction()
                .replace(R.id.drawerFragmentContainer, NewValueTransferFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {

        val adapter = ValueTransferAdapter(dataset,
                { receipt -> presenter.onTransferValueClick(receipt) },
                { receipt, position ->
                    dataset.find { it.address == receipt.address }?.receipts?.remove(receipt)
                    adapter?.notifyDataSetChanged()
                    presenter.onReceiptDeleted(receipt)
                })

        adapter.shouldShowHeadersForEmptySections(false)

        return adapter
    }

    override fun initViews() {
        super.initViews()
    }
}

