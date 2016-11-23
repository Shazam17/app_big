package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import org.jetbrains.anko.alert
import javax.inject.Inject


class AutoPaymentsListFragment : BaseListFragment<Receipt>(), IAutoPaymentsListView {

    @Inject lateinit var presenter: IAutoPaymentsListPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerAutoPaymentsListComponent.builder()
                .appComponent(appComponent)
                .autoPaymentsListModule(AutoPaymentsListModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return AutoPaymentsListAdapter(dataset,
                { receipt -> presenter.onEditButtonClick(receipt) },
                { receipt -> presenter.onDeleteButtonClick(receipt) })
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun navigateToEditAutoPayment(receipt: Receipt) {
        //TODO NavigateToEditAutoPayment
        showMessage("TODO: NavigateToEditAutoPayment " + receipt.barcode)
    }

    override fun showConfirmDeleteDialog(receipt: Receipt) {
        alert(R.string.auto_payments_delete_autopay_dialog_description, R.string.auto_payments_delete_dialog_title) {
            okButton { presenter.onConfirmDelete(receipt) }
            cancelButton { autoPaymentDidNotDeleted(receipt) }
        }.show()
    }

    override fun autoPaymentDidNotDeleted(receipt: Receipt) {
        adapter?.notifyItemChanged(dataset.indexOf(receipt))
    }

    override fun autoPaymentDeleted(receipt: Receipt) {
        val receiptIndex = dataset.indexOf(receipt)
        dataset.removeAt(receiptIndex)
        adapter?.notifyItemRemoved(receiptIndex)
    }
}
