package com.software.ssp.erkc.modules.valuetransfer

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist.ValueTransferListFragment
import org.jetbrains.anko.withArguments
import javax.inject.Inject

class ValueTransferFragment : MvpFragment(), IValueTransferView {

    @Inject lateinit var presenter: IValueTransferPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_with_container, container, false)  // todo change
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
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun navigateToAddReceiptScreen() {
        navigateTo(NewReceiptFragment().withArguments("isTransferValue" to true, "isTransferValueVisible" to false))
    }

    override fun navigateToValueTransferListScreen() {
        navigateTo(ValueTransferListFragment())
    }

    private fun navigateTo(fragment: Fragment) {
        childFragmentManager.beginTransaction()
                .replace(R.id.containerLayout, fragment)
                .commit()
    }
}
