package com.software.ssp.erkc.modules.history.valueshistorylist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.delegates.args
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.modules.history.IHistoryListDelegate
import com.software.ssp.erkc.modules.history.filter.FilterChipTag
import com.software.ssp.erkc.modules.history.filter.HistoryFilterActivity
import com.software.ssp.erkc.modules.history.filter.HistoryFilterField
import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import com.software.ssp.erkc.modules.history.valuehistory.ValueHistoryActivity
import kotlinx.android.synthetic.main.fragment_history_list.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject


class ValuesHistoryListFragment : BaseListFragment<RealmReceipt>(), IValuesHistoryListView, IHistoryListDelegate {

    @Inject lateinit var presenter: IValuesHistoryListPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerValuesHistoryListComponent.builder()
                .appComponent(appComponent)
                .valuesHistoryListModule(ValuesHistoryListModule(this))
                .build()
                .inject(this)
    }

    private var historyFilter: HistoryFilterModel by args(defaultValue = HistoryFilterModel())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_history_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.currentFilter = historyFilter
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            HistoryFilterActivity.REQUEST_CODE -> {
                historyFilter = data?.getParcelableExtra(HistoryFilterActivity.RESULT_KEY) ?: HistoryFilterModel()
                presenter.currentFilter = historyFilter
            }
        }
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValuesHistoryListAdapter(
                dataset,
                { receipt -> presenter.onReceiptClick(receipt) }
        )
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun showCurrentFilter(currentFilter: HistoryFilterModel) {
        filterChipView.chipList.clear()

        checkAndAddFilterTag(currentFilter.barcode, HistoryFilterField.BARCODE)
        checkAndAddFilterTag(currentFilter.street, HistoryFilterField.STREET)
        checkAndAddFilterTag(currentFilter.house, HistoryFilterField.HOUSE)
        checkAndAddFilterTag(currentFilter.apartment, HistoryFilterField.APARTMENT)
        checkAndAddFilterTag(currentFilter.deviceNumber, HistoryFilterField.DEVICE_NUMBER)
        checkAndAddFilterTag(currentFilter.deviceInstallPlace, HistoryFilterField.DEVICE_PLACE)

        currentFilter.periodFrom?.let {
            checkAndAddFilterTag("%s - %s".format(it.toString(Constants.RECEIPT_DATE_FORMAT), currentFilter.periodTo!!.toString(Constants.RECEIPT_DATE_FORMAT)), HistoryFilterField.PERIOD)
        }

        filterChipView.refresh()

        filterChipView.visibility = if (filterChipView.chipList.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun navigateToIpuValueInfo(receipt: RealmReceipt) {
        startActivity<ValueHistoryActivity>(Constants.KEY_HISTORY_FILTER to historyFilter, Constants.KEY_RECEIPT to receipt.id)
    }

    override fun onFilterClick() {
        presenter.onFilterClick()
    }

    override fun onRefreshClick() {
        presenter.onRefreshClick()
    }

    override fun navigateToFilter(currentFilter: HistoryFilterModel) {
        startActivityForResult<HistoryFilterActivity>(
                HistoryFilterActivity.REQUEST_CODE,
                HistoryFilterActivity.KEY_CURRENT_FILTER to currentFilter,
                "isPaymentFilter" to false
        )
    }

    override fun initViews() {
        super.initViews()
        swipeToRefreshEnabled = false
        filterChipView.chipLayoutRes = R.layout.layout_history_chip
        filterChipView.setOnChipClickListener {
            chip ->
            presenter.onFilterDeleted((chip as FilterChipTag).field)
            filterChipView.remove(chip)

            if (filterChipView.chipList.isEmpty()) {
                filterChipView.visibility = View.GONE
            }
        }
    }

    override fun setEmptyViewVisible(visible: Boolean) {
        emptyView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    private fun checkAndAddFilterTag(text: String?, field: HistoryFilterField) {
        if (!text.isNullOrBlank()) {
            filterChipView.add(FilterChipTag(text!!, field))
        }
    }
}
