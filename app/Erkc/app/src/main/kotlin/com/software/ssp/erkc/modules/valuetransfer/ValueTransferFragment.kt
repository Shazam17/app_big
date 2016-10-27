package com.software.ssp.erkc.modules.valuetransfer

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.di.AppComponent
import javax.inject.Inject

class ValueTransferFragment : BaseListFragment<Receipt, IValueTransferView, IValueTransferPresenter>(), IValueTransferView {

    @Inject lateinit var presenter: IValueTransferPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_non_authed_main_screen, container, false)
    }

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
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onSwipeToRefresh() {

    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return ValueTransferAdapter(dataset,
                { device -> presenter.onItemClick(device) },
                { position ->
                    //TODO On TransferValue button click
                })
    }

    override fun initViews() {
        super.initViews()

    }
}

