package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class RealmNotification(
        @PrimaryKey
        open var id: String = "",
        open var isRead: Boolean = false,
        open var message: String = "",
        open var title: String = "",
        open var deliveredDate: Date? = null,
        open var readDate: Date? = null
) : RealmObject()
