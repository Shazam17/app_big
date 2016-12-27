package com.software.ssp.erkc.modules.notifications.filter

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import java.util.*


interface INotificationsFilterView : IView {
    fun showSelectPeriodFromDialog(date: Date)
    fun showSelectPeriodToDialog(date: Date)

    fun showSelectedPeriod(dateFrom: Date, dateTo: Date)

    fun showCurrentFilter(currentFilter: NotificationsFilterModel)

    fun applyFilter(currentFilter: NotificationsFilterModel)
}

interface INotificationsFilterPresenter : IPresenter<INotificationsFilterView> {
    var currentFilter: NotificationsFilterModel

    fun onSelectPeriodClick()
    fun onApplyFilterClick()

    fun onTitleTextChanged(title: String)
    fun onMessageTextChanged(message: String)
    fun onReadSwitchChanged(isRead: Boolean)

    fun onPeriodDateFromSelected(date: Date)
    fun onPeriodDateToSelected(date: Date)
}
