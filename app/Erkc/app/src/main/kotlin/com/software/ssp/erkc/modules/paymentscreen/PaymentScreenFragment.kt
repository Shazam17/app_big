package com.software.ssp.erkc.modules.paymentscreen

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.newreceipt.NewReceiptFragment
import com.software.ssp.erkc.modules.paymentscreen.paymentlist.PaymentListFragment
import org.jetbrains.anko.withArguments
import javax.inject.Inject

class PaymentScreenFragment : MvpFragment(), IPaymentScreenView {

    @Inject lateinit var presenter: IPaymentScreenPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_with_container, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerPaymentScreenComponent.builder()
                .appComponent(appComponent)
                .paymentScreenModule(PaymentScreenModule(this))
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
        navigateTo(NewReceiptFragment().withArguments("isTransferValueVisible" to false))
    }

    override fun navigateToPaymentsList() {
        navigateTo(PaymentListFragment())
    }

    private fun navigateTo(fragment: Fragment) {
        childFragmentManager.beginTransaction()
                .replace(R.id.containerLayout, fragment)
                .commit()
    }
}
