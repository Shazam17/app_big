package com.software.ssp.erkc.modules.notifications.notificationslist

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmNotification
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.MessagesRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterField
import com.software.ssp.erkc.modules.notifications.filter.NotificationsFilterModel
import rx.lang.kotlin.plusAssign
import rx.lang.kotlin.toObservable
import javax.inject.Inject


class NotificationsListPresenter @Inject constructor(view: INotificationsListView) : RxPresenter<INotificationsListView>(view), INotificationsListPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var messagesRepository: MessagesRepository

    override var currentFilter: NotificationsFilterModel = NotificationsFilterModel()
        set(value) {
            field = value
            view?.showCurrentFilter(value)
            showNotificationsList()
        }

    override fun onViewAttached() {
        view?.showCurrentFilter(currentFilter)
    }

    override fun onResume() {
        showNotificationsList()
    }

    override fun onSwipeToRefresh() {
        subscriptions += messagesRepository
                .fetchMessagesByUser()
                .concatMap {
                    notifications ->
                    realmRepository.saveNotificationsList(notifications ?: emptyList())
                }
                .subscribe(
                        {
                            showNotificationsList()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onNotificationClick(notification: RealmNotification) {
        view?.navigateToNotification(notification)
    }

    override fun onNotificationDeleteClick(notification: RealmNotification) {
        subscriptions += messagesRepository
                .deleteMessage(notification.id)
                .concatMap {
                    realmRepository.removeNotification(notification)
                }
                .subscribe(
                        {
                            view?.notifyItemRemoved(notification)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    override fun onFilterDeleted(filterField: NotificationsFilterField) {
        when (filterField) {
            NotificationsFilterField.TITLE -> currentFilter.title = ""
            NotificationsFilterField.MESSAGE -> currentFilter.message = ""
            NotificationsFilterField.PERIOD -> {
                currentFilter.periodFrom = null
                currentFilter.periodTo = null
            }
            NotificationsFilterField.READ -> currentFilter.isRead = false
        }
        showNotificationsList()
    }

    override fun onSearchSubmit(query: String) {
        currentFilter.title = query
        view?.showCurrentFilter(currentFilter)
        showNotificationsList()
    }

    override fun onFilterClick() {
        view?.navigateToFilter(currentFilter)
    }

    private fun showNotificationsList() {
        subscriptions += realmRepository
                .fetchNotificationsList()
                .flatMap { it.toObservable() }
                .filter {
                    notification ->

                    when {
                        !currentFilter.title.isNullOrBlank() && !notification.title.contains(currentFilter.title, true) -> return@filter false
                        !currentFilter.message.isNullOrBlank() && !notification.message.contains(currentFilter.message, true) -> return@filter false
                        currentFilter.isRead && !notification.isRead -> return@filter false
                    }

                    currentFilter.periodFrom?.let {
                        if (notification.deliveredDate != null && (notification.deliveredDate!! < it || notification.deliveredDate!! > currentFilter.periodTo!!)) {
                            return@filter false
                        }
                    }

                    return@filter true
                }
                .toList()
                .subscribe(
                        {
                            notifications ->
                            view?.showData(notifications)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                            view?.setLoadingVisible(false)
                        }
                )
    }
}
