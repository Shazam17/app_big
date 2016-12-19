package com.software.ssp.erkc.modules.transaction.valuestransaction

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.sendvalues.SendValuesActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * @author Alexander Popov on 14/12/2016.
 */
class ValuesTransactionListFragment : BaseListFragment<RealmReceipt>(), IValuesTransactionListView {

    @Inject lateinit var presenter: IValuesTransactionListPresenter

    override fun navigateToIpuValueInfo(receipt: RealmReceipt) {
        startActivity<SendValuesActivity>(Constants.KEY_RECEIPT to receipt.id, Constants.KEY_FROM_TRANSACTION to true)
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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValuesTransactionListAdapter(dataset, object : ValuesTransactionListAdapter.InteractionListener {
            override fun onSendValuesClick(receipt: RealmReceipt) {
                presenter.onIpuClick(receipt)
            }

            override fun deleteClick(receipt: RealmReceipt) {
                presenter.onDeleteClick(receipt)
            }

        })
    }

    override fun initViews() {
        super.initViews()
        swipeToRefreshEnabled = false
    }

}