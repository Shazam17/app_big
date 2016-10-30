package com.software.ssp.erkc.modules.valuetransfer

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import javax.inject.Inject

class ValueTransferFragment : BaseListFragment<ReceiptsViewModel, IValueTransferView, IValueTransferPresenter>(), IValueTransferView {

    @Inject lateinit var presenter: IValueTransferPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerValueTransferComponent.builder()
                .appComponent(appComponent)
                .valueTransferModule(ValueTransferModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        presenter.onViewAttached()
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

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValueTransferAdapter(dataset,
                { receipt -> presenter.onTransferValueClick(receipt) })
    }

    override fun initViews() {
        super.initViews()
    }
}

