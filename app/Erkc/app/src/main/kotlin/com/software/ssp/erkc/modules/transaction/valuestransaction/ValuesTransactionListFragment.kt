package com.software.ssp.erkc.modules.transaction.valuestransaction

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmOfflineIpu
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.sendvalues.SendValuesActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 14/12/2016.
 */
class ValuesTransactionListFragment : BaseListFragment<RealmOfflineIpu>(), IValuesTransactionListView, ValuesTransactionListAdapter.InteractionListener {

    @Inject lateinit var presenter: IValuesTransactionListPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerValuesTransactionListComponent.builder()
                .appComponent(appComponent)
                .valuesTransactionListModule(ValuesTransactionListModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onSwipeToRefresh() {
        //disabled
    }

    override fun onSendValuesClick(offlineIpu: RealmOfflineIpu) {
        presenter.onIpuClick(offlineIpu)
    }

    override fun onDeleteOfflineIpuClick(offlineIpu: RealmOfflineIpu) {
        presenter.onDeleteClick(offlineIpu)
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValuesTransactionListAdapter(dataset, this)
    }

    override fun navigateToIpuValueInfo(realmOfflineIpu: RealmOfflineIpu) {
        startActivity<SendValuesActivity>(Constants.KEY_RECEIPT to realmOfflineIpu.receipt.id, Constants.KEY_FROM_TRANSACTION to true)
    }

    override fun initViews() {
        super.initViews()
        swipeToRefreshEnabled = false
    }
}
