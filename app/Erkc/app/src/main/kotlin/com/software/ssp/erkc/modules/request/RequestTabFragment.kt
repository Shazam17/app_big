package com.software.ssp.erkc.modules.request

import android.app.AlertDialog
import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v13.app.FragmentStatePagerAdapter
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.createrequest.CreateRequestActivity
import com.software.ssp.erkc.modules.history.filter.FilterChipTag
import com.software.ssp.erkc.modules.request.authedRequest.activeRequestList.ActiveRequestListFragment
import com.software.ssp.erkc.modules.request.authedRequest.archiveRequestList.ArchiveRequestListFragment
import com.software.ssp.erkc.modules.request.authedRequest.draftRequestList.DraftRequestListFragment
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.FilterRequestChipTag
import com.software.ssp.erkc.modules.request.authedRequest.filterRequest.StatusModel
import kotlinx.android.synthetic.main.activity_search_address.*
import kotlinx.android.synthetic.main.fragment_history_tab.tabsViewPaper
import kotlinx.android.synthetic.main.fragment_request_list.*
import kotlinx.android.synthetic.main.fragment_request_tab.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class RequestTabFragment : MvpFragment(), IRequestTabView {

    @Inject
    lateinit var presenter: IRequestTabPresenter

    enum class TabItem(val titleResId: Int) {
        ACTIVE(R.string.request_active_tab_title),
        ARCHIVE(R.string.request_archive_tab_title),
        DRAFT(R.string.request_draft_tab_title);
    }

    private val currentTabRequestList: IRequestListDelegate
        get() = tabsViewPaper.adapter?.instantiateItem(container, tabsViewPaper.currentItem) as IRequestListDelegate

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerRequestTabComponent.builder()
                .appComponent(appComponent)
                .requestTabModule(RequestTabModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_request_tab, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.request_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun refreshCurrentList() {
        // TODO implementation
    }

    private fun navigateToCreateRequestScreen() {
        activity.startActivity<CreateRequestActivity>()
    }


    private fun initViews() {

        tabLayout.addTab(tabLayout.newTab().setText(TabItem.ARCHIVE.titleResId).setCustomView(R.layout.notification_badge_active_tab))
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.ARCHIVE.titleResId))
        tabLayout.addTab(tabLayout.newTab().setText(TabItem.DRAFT.titleResId))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

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
                    TabItem.ACTIVE -> ActiveRequestListFragment()
                    TabItem.ARCHIVE -> ArchiveRequestListFragment()
                    TabItem.DRAFT -> DraftRequestListFragment()
                }
            }
        }
        requestTapCreateRequestButton.onClick {
            navigateToCreateRequestScreen()
        }

    }

}