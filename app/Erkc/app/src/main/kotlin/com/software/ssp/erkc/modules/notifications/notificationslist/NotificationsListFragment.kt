package com.software.ssp.erkc.modules.notifications.notificationslist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.BaseListFragment
import com.software.ssp.erkc.data.realm.models.RealmNotification
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.extensions.toString
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterActivity
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterChipTag
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterField
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterModel
import com.software.ssp.erkc.modules.notifications.notificationscreen.NotificationScreenActivity
import kotlinx.android.synthetic.main.fragment_history_list.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import javax.inject.Inject


class NotificationsListFragment : BaseListFragment<RealmNotification>(), INotificationsListView, NotificationsListAdapter.InteractionListener {

    @Inject lateinit var presenter: INotificationsListPresenter

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerNotificationsListComponent.builder()
                .appComponent(appComponent)
                .notificationsListModule(NotificationsListModule(this))
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_notifications_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
        inflater?.inflate(R.menu.notifications_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_filter -> {
                presenter.onFilterClick()
                return true
            }
            R.id.menu_search -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            NotificationsFilterActivity.REQUEST_CODE -> {
                presenter.currentFilter = data?.getParcelableExtra(NotificationsFilterActivity.RESULT_KEY) ?: NotificationsFilterModel()
            }
        }
    }

    override fun notificationDeleteClick(notification: RealmNotification) {
        presenter.onNotificationDeleteClick(notification)
    }

    override fun notificationClick(notification: RealmNotification) {
        presenter.onNotificationClick(notification)
    }

    override fun createAdapter(): RecyclerView.Adapter<*> {
        return NotificationsListAdapter(dataset, this)
    }

    override fun onSwipeToRefresh() {
        presenter.onSwipeToRefresh()
    }

    override fun showCurrentFilter(currentFilter: NotificationsFilterModel) {
        filterChipView.chipList.clear()

        checkAndAddFilterTag(currentFilter.title, NotificationsFilterField.TITLE)
        checkAndAddFilterTag(currentFilter.message, NotificationsFilterField.MESSAGE)

        if (currentFilter.isRead) {
            checkAndAddFilterTag(getString(R.string.notifications_filter_read_status), NotificationsFilterField.READ)
        }

        currentFilter.periodFrom?.let {
            checkAndAddFilterTag("%s - %s".format(it.toString(Constants.RECEIPT_DATE_FORMAT), currentFilter.periodTo!!.toString(Constants.RECEIPT_DATE_FORMAT)), NotificationsFilterField.PERIOD)
        }

        filterChipView.refresh()

        filterChipView.visibility = if (filterChipView.chipList.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun navigateToFilter(currentFilter: NotificationsFilterModel) {
        startActivityForResult<NotificationsFilterActivity>(
                NotificationsFilterActivity.REQUEST_CODE,
                NotificationsFilterActivity.KEY_CURRENT_FILTER to currentFilter
        )
    }

    override fun navigateToNotification(notification: RealmNotification) {
        startActivity<NotificationScreenActivity>("notificationId" to notification.id)
    }

    override fun initViews() {
        super.initViews()
        filterChipView.chipLayoutRes = R.layout.layout_history_chip
        filterChipView.setOnChipClickListener {
            chip ->
            presenter.onFilterDeleted((chip as NotificationsFilterChipTag).field)
            filterChipView.remove(chip)

            if (filterChipView.chipList.isEmpty()) {
                filterChipView.visibility = View.GONE
            }
        }
    }

    private fun checkAndAddFilterTag(text: String?, field: NotificationsFilterField) {
        if (!text.isNullOrBlank()) {
            filterChipView.add(NotificationsFilterChipTag(text!!, field))
        }
    }
}
