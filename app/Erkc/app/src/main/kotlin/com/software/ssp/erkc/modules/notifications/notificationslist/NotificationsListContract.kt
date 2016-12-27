package com.software.ssp.erkc.modules.notifications.notificationslist

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmNotification
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterField
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterModel


interface INotificationsListView : IListView<RealmNotification> {
    fun showCurrentFilter(currentFilter: NotificationsFilterModel)
    fun navigateToFilter(currentFilter: NotificationsFilterModel)
    fun navigateToNotification(notification: RealmNotification)
}

interface INotificationsListPresenter : IListPresenter<RealmNotification, INotificationsListView> {
    var currentFilter: NotificationsFilterModel

    fun onNotificationClick(notification: RealmNotification)
    fun onNotificationDeleteClick(notification: RealmNotification)

    fun onFilterDeleted(filterField: NotificationsFilterField)
    fun onFilterClick()

    fun onResume()
}
