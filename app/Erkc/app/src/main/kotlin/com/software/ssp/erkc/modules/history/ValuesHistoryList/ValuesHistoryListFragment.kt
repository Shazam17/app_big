package com.software.ssp.erkc.modules.history.ValuesHistoryList

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.history.IHistoryListDelegate
import kotlinx.android.synthetic.main.fragment_history_list.*
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
        return inflater?.inflate(R.layout.fragment_history_list, container, false)
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

    override fun navigateToIpuValueInfo(receipt: RealmReceipt) {
        showMessage("TODO") //TODO
    }

    override fun onFilterClick() {
        showMessage("TODO Filter by values")
        //TODO start activity for result
    }

    override fun initViews() {
        super.initViews()
        emptyMessageText = ""
        filterChipView.chipLayoutRes = R.layout.layout_history_chip
    }
}
