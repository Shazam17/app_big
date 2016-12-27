package com.software.ssp.erkc.modules.notifications.notificationscreen

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.RealmNotification
import java.util.*


interface INotificationScreenView : IView {
    fun showNotificationInfo(notification: RealmNotification)
    fun showReadDate(date: Date)
    fun showShareDialog(notification: RealmNotification)
}

interface INotificationScreenPresenter : IPresenter<INotificationScreenView> {
    var notificationId: String
    fun onShareClick()
}
