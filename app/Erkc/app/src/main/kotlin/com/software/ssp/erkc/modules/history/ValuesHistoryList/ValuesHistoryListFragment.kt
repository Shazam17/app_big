package com.software.ssp.erkc.modules.history.ValuesHistoryList

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.history.IHistoryListDelegate
import com.software.ssp.erkc.modules.history.valuehistory.ValueHistoryActivity
import org.jetbrains.anko.startActivity
import java.util.*
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
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

    override fun navigateToIpuValueInfo(receipt: RealmReceipt, dateFrom: Date, dateTo: Date) {
        startActivity<ValueHistoryActivity>(Constants.KEY_DATE_FROM to dateFrom, Constants.KEY_DATE_TO to dateTo, Constants.KEY_RECEIPT to receipt.id)
    }

    override fun navigateToFilter() {
        showMessage("TODO Filter by values")
        //TODO start activity for result
    }

    override fun initViews() {
        super.initViews()
        emptyMessageText = ""
    }
}
