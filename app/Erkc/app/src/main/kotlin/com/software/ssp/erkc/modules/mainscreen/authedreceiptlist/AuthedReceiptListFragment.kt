package com.software.ssp.erkc.modules.mainscreen.authedreceiptlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import javax.inject.Inject

class AuthedReceiptListFragment : MvpFragment(), IAuthedReceiptListView {

    @Inject lateinit var presenter: IAuthedReceiptListPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_main_authed_receiptlist, container, false)  // todo change
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerAuthedReceiptListScreenComponent.builder()
                .appComponent(appComponent)
                .authedReceiptListScreenModule(AuthedReceiptListScreenModule(this))
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

    override fun navigateToAddReceiptScreen() {
        // todo
    }

    override fun navigateToIPUinputScreen() {
        // todo
    }

    override fun navigateToPayScreen() {
        // todo
    }

    private fun initViews() {
        // todo
    }

}
