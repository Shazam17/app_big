package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class OfflineSettings(
        @PrimaryKey
        open var login: String = "",
        open var password: String = "",
        open var offlineModeEnabled: Boolean = false,
        open var pushEnabled: Boolean = false,
        open var operationStatusNotificationEnabled: Boolean = false,
        open var newsNotificationEnabled: Boolean = false,
        open var paymentNotificationEnabled: Boolean = false,
        open var ipuNotificationEnabled: Boolean = false

) : RealmObject() {
}