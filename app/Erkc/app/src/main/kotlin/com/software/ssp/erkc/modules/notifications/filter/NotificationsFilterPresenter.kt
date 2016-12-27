package com.software.ssp.erkc.modules.notifications.filter

import com.software.ssp.erkc.common.mvp.RxPresenter
import java.util.*
import javax.inject.Inject


class NotificationsFilterPresenter @Inject constructor(view: INotificationsFilterView) : RxPresenter<INotificationsFilterView>(view), INotificationsFilterPresenter {

    override lateinit var currentFilter: NotificationsFilterModel

    override fun onViewAttached() {
        super.onViewAttached()
        view?.showCurrentFilter(currentFilter)
    }

    override fun onSelectPeriodClick() {
        view?.showSelectPeriodFromDialog(currentFilter.periodFrom ?: Calendar.getInstance().time)
    }

    override fun onApplyFilterClick() {
        view?.applyFilter(currentFilter)
    }

    override fun onTitleTextChanged(title: String) {
        currentFilter.title = title
    }

    override fun onMessageTextChanged(message: String) {
        currentFilter.message = message
    }

    override fun onReadSwitchChanged(isRead: Boolean) {
        currentFilter.isRead = isRead
    }

    override fun onPeriodDateFromSelected(date: Date) {
        currentFilter.periodFrom = date
        view?.showSelectPeriodToDialog(date)
    }

    override fun onPeriodDateToSelected(date: Date) {
        currentFilter.periodTo = date
        view?.showSelectedPeriod(currentFilter.periodFrom!!, currentFilter.periodTo!!)
    }
}
