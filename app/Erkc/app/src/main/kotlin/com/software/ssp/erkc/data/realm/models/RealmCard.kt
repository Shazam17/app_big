package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class RealmCard(
        @PrimaryKey
        open var id: String = "",
        open var name: String = "",
        open var statusId: Int = 0,
        open var remoteCardId: String? = null,
        open var maskedCardNumber: String? = null,
        open var statusStr: String = "") : RealmObject()
