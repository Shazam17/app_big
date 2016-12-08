package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey


open class RealmSettings(
        @PrimaryKey
        open var login: String = "",
        password: String = "",
        open var offlineModeEnabled: Boolean = false,
        open var pushEnabled: Boolean = false,
        open var operationStatusNotificationEnabled: Boolean = false,
        open var newsNotificationEnabled: Boolean = false,
        open var paymentNotificationEnabled: Boolean = false,
        open var ipuNotificationEnabled: Boolean = false
) : RealmObject() {

    @Ignore
    var password: String = ""
        set(value) {
            field = value
            passwordHash = value.hashCode()
        }

    var passwordHash: Int

    init {
        passwordHash = password.hashCode()
    }

    fun checkPassword(password: String): Boolean {
        return password.hashCode() == passwordHash
    }
}
