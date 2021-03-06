package com.software.ssp.erkc.modules.autopayments

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v13.app.FragmentStatePagerAdapter
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.autopayments.autopaymentslist.AutoPaymentsListFragment
import com.software.ssp.erkc.modules.autopayments.settings.AutoPaymentSettingsActivity
import com.software.ssp.erkc.modules.instructions.InstructionType
import com.software.ssp.erkc.modules.instructions.instructiondetails.InstructionActivity
import kotlinx.android.synthetic.main.fragment_auto_payments_tab.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.withArguments
import javax.inject.Inject


class AutoPaymentsTabFragment : MvpFragment(), IAutoPaymentsTabView {

    @Inject lateinit var presenter: IAutoPaymentsTabPresenter

    enum class TabItem(val titleResId: Int) {
        AUTO_PAYMENTS(R.string.auto_payments_autopay_tab_title),
        ONE_CLICK_PAYMENT(R.string.auto_payments_one_click_tab_title)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerAutoPaymentsTabComponent.builder()
                .appComponent(appComponent)
                .autoPaymentsTabModule(AutoPaymentsTabModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_auto_payments_tab, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
        inflater?.inflate(R.menu.autopayments_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_add -> {
                presenter.onAddNewAutoPaymentClick()
                return true
            }
            R.id.menu_help -> {
                presenter.onInfoClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun navigateToNewAutoPayment() {
        startActivity<AutoPaymentSettingsActivity>()
    }

    override fun navigateToInstruction() {
        startActivity<InstructionActivity>("instructionType" to InstructionType.AUTO_PAYMENTS)
    }

    private fun initViews() {
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.AUTO_PAYMENTS.titleResId))
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.ONE_CLICK_PAYMENT.titleResId))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                tabsViewPaper.currentItem = tab.position
            }
        })

        tabsViewPaper.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            override fun getCount(): Int {
                return tabLayout.tabCount
            }

            override fun getItem(position: Int): Fragment {
                return when (TabItem.values()[position]) {
                    TabItem.AUTO_PAYMENTS -> AutoPaymentsListFragment().withArguments("autoPaymentMode" to 2)
                    TabItem.ONE_CLICK_PAYMENT -> AutoPaymentsListFragment().withArguments("autoPaymentMode" to 1)
                }
            }
        }
    }
}
