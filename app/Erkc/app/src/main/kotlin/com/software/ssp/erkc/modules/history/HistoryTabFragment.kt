package com.software.ssp.erkc.modules.history

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v13.app.FragmentStatePagerAdapter
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.history.PaymentHistoryList.PaymentHistoryListFragment
import com.software.ssp.erkc.modules.history.ValuesHistoryList.ValuesHistoryListFragment
import kotlinx.android.synthetic.main.fragment_history_tab.*
import javax.inject.Inject


class HistoryTabFragment : MvpFragment(), IHistoryTabView {

    @Inject lateinit var presenter: IHistoryTabPresenter

    enum class TabItem(val titleResId: Int) {
        PAYMENTS_HISTORY(R.string.history_payments_tab_title),
        VALUES_HISTORY(R.string.history_values_tab_title)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerHistoryTabComponent.builder()
                .appComponent(appComponent)
                .historyTabModule(HistoryTabModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_history_tab, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
        inflater?.inflate(R.menu.history_menu, menu)
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
            R.id.menu_filter -> {

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.PAYMENTS_HISTORY.titleResId))
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.VALUES_HISTORY.titleResId))
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

        tabsViewPaper.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabsViewPaper.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            override fun getCount(): Int {
                return tabLayout.tabCount
            }

            override fun getItem(position: Int): Fragment {
                return when (TabItem.values()[position]) {
                    TabItem.PAYMENTS_HISTORY -> PaymentHistoryListFragment()
                    TabItem.VALUES_HISTORY -> ValuesHistoryListFragment()
                }
            }
        }
    }
}
