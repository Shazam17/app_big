package com.software.ssp.erkc.modules.transaction

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v13.app.FragmentStatePagerAdapter
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.modules.history.HistoryTabFragment
import com.software.ssp.erkc.modules.transaction.paymenttransaction.PaymentTransactionListFragment
import com.software.ssp.erkc.modules.transaction.valuestransaction.ValuesTransactionListFragment
import kotlinx.android.synthetic.main.fragment_history_tab.*

/**
 * @author Alexander Popov on 13/12/2016.
 */
class TransactionTabFragment : Fragment() {
    enum class TabItem(val titleResId: Int) {
        PAYMENTS_TRANSACTION(R.string.transaction_payments_tab_title),
        VALUES_TRANSACTION(R.string.transaction_values_tab_title)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_transaction_tab, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initViews() {
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.PAYMENTS_TRANSACTION.titleResId))
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.VALUES_TRANSACTION.titleResId))
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
                return when (HistoryTabFragment.TabItem.values()[position]) {
                    HistoryTabFragment.TabItem.PAYMENTS_HISTORY -> PaymentTransactionListFragment()
                    HistoryTabFragment.TabItem.VALUES_HISTORY -> ValuesTransactionListFragment()
                }
            }
        }
    }
}