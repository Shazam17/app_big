package com.software.ssp.erkc.modules.mainscreen.receiptlist


import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.common.receipt.ReceiptViewModel
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.materialDialog
import com.software.ssp.erkc.modules.autopayments.settings.AutoPaymentSettingsActivity
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import com.software.ssp.erkc.modules.paymentscreen.payment.PaymentActivity
import com.software.ssp.erkc.modules.sendvalues.SendValuesActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject


class ReceiptListFragment : BaseListFragment<ReceiptViewModel>(), IReceiptListView {

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

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ReceiptListAdapter(dataset,
                object : ReceiptListAdapter.InteractionListener {
                    override fun paymentClick(receipt: RealmReceipt) {
                        presenter.onPayButtonClick(receipt)
                    }

                    override fun transferClick(receipt: RealmReceipt) {
                        presenter.onTransferButtonClick(receipt)
                    }

                    override fun menuClick(menuId: Int, receipt: RealmReceipt) {
                        when (menuId) {
                            R.id.menuHistory -> presenter.onHistoryButtonClick(receipt)
                            R.id.menuAutoPay -> presenter.onAutoPaymentButtonClick(receipt)
                        }
                    }

                    override fun deleteClick(receipt: RealmReceipt) {
                        presenter.onReceiptDeleted(receipt)
                    }
                })
    }

    override fun showNoActivatedCardsDialog() {
        activity.materialDialog {
            content(R.string.main_screen_no_cards_dialog_text)
            positiveText(R.string.main_screen_no_cards_dialog_ok_button)
        }.show()
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

    override fun navigateToIPUInputScreen(receipt: RealmReceipt) {
        startActivity<SendValuesActivity>("receiptId" to receipt.id)
    }

    override fun navigateToPayScreen(receipt: RealmReceipt) {
        startActivity<PaymentActivity>("receiptId" to receipt.id)
    }

    override fun navigateToAutoPaymentSettingScreen(receiptId: String) {
        startActivity<AutoPaymentSettingsActivity>("receiptId" to receiptId)
    }
}
