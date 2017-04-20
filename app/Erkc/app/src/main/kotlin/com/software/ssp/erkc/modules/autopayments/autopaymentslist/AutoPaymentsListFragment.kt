package com.software.ssp.erkc.modules.autopayments.autopaymentslist

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.args
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.modules.autopayments.settings.AutoPaymentSettingsActivity
import kotlinx.android.synthetic.main.fragment_cards.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject


class AutoPaymentsListFragment : BaseListFragment<ReceiptViewModel>(), IAutoPaymentsListView, AutoPaymentsListAdapter.InteractionListener {

    @Inject lateinit var presenter: IAutoPaymentsListPresenter

    private val autoPaymentMode: Int by args(defaultValue = 0)

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerAutoPaymentsListComponent.builder()
                .appComponent(appComponent)
                .autoPaymentsListModule(AutoPaymentsListModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_auto_payments_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.autoPaymentMode = autoPaymentMode
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onAutoPaymentEditClick(receipt: RealmReceipt) {
        presenter.onEditButtonClick(receipt)
    }

    override fun onAutoPaymentDeleteClick(receipt: RealmReceipt) {
        presenter.onDeleteButtonClick(receipt)
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return AutoPaymentsListAdapter(dataset, this)
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun setEmptyViewVisible(visible: Boolean) {
        emptyView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun showConfirmDeleteDialog(receipt: RealmReceipt) {
        activity.materialDialog {
            title(R.string.auto_payments_delete_dialog_title)
            content(if (autoPaymentMode == 1) R.string.auto_payments_delete_one_click_dialog_description else R.string.auto_payments_delete_autopay_dialog_description)
            positiveText(R.string.auto_payments_delete_positive_button)
            onPositive { _, _ ->
                presenter.onConfirmDelete(receipt)
            }
            negativeText(R.string.auto_payments_delete_negative_button)
            onNegative { _, _ ->
                autoPaymentDidNotDeleted(receipt)
            }
        }.show()
    }

    override fun autoPaymentDidNotDeleted(receipt: RealmReceipt) {
        val receiptIndex = dataset.indexOfFirst { it.receipt == receipt }
        dataset[receiptIndex].isRemovePending = false
        adapter?.notifyItemChanged(receiptIndex)
    }

    override fun autoPaymentDeleted(receipt: RealmReceipt) {
        val receiptIndex = dataset.indexOfFirst { it.receipt == receipt }
        dataset.removeAt(receiptIndex)
        adapter?.notifyItemRemoved(receiptIndex)
        if (dataset.count() == 0) {
            setEmptyViewVisible(true)
        }
    }

    override fun navigateToEditAutoPayment(receipt: RealmReceipt) {
        startActivity<AutoPaymentSettingsActivity>("receiptId" to receipt.id)
    }
}
