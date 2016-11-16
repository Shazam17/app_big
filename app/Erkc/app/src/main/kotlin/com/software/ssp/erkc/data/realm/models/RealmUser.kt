package com.software.ssp.erkc.data.realm.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class RealmUser(
        @PrimaryKey
        open var login: String = "",
        open var email: String = "",
        open var name: String = "",
        open var isCurrentUser: Boolean = false,
        open var settings: OfflineUserSettings? = null,
        open var receipts: RealmList<RealmReceipt> = RealmList()) : RealmObject()