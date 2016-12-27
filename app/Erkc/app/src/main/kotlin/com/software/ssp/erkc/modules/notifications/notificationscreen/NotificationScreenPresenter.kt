package com.software.ssp.erkc.modules.notifications.notificationscreen

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmNotification
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.MessagesRepository
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.lang.kotlin.plusAssign
import java.util.*
import javax.inject.Inject


class NotificationScreenPresenter @Inject constructor(view: INotificationScreenView) : RxPresenter<INotificationScreenView>(view), INotificationScreenPresenter {

    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var messagesRepository: MessagesRepository

    override var notificationId: String = ""

    private var notification: RealmNotification? = null

    override fun onViewAttached() {
        super.onViewAttached()
        fetchNotification()
    }

    override fun onViewDetached() {
        super.onViewDetached()
        realmRepository.close()
    }

    override fun onShareClick() {
        view?.showShareDialog(notification!!)
    }

    private fun fetchNotification() {
        subscriptions += realmRepository
                .fetchNotificationById(notificationId)
                .concatMap {
                    notification ->
                    this.notification = notification

                    view?.showNotificationInfo(notification)

                    if (activeSession.isOfflineSession || notification.isRead) {
                        Observable.empty()
                    } else {
                        messagesRepository.setMessageAsRead(notificationId)
                    }
                }
                .concatMap {
                    notification!!.isRead = true
                    notification!!.readDate = Calendar.getInstance().time
                    realmRepository.updateNotification(notification!!)
                }
                .subscribe(
                        {
                            view?.showReadDate(notification!!.readDate!!)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }
}
