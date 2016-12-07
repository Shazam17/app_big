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
        open var receipts: RealmList<RealmReceipt> = RealmList(),
        open var cards: RealmList<RealmCard> = RealmList(),
        open var payments: RealmList<RealmPayment> = RealmList(),
        open var paymentsInfo: RealmList<RealmPaymentInfo> = RealmList(),
        open var ipus: RealmList<RealmIpu> = RealmList()) : RealmObject()