package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class RealmIpuValue(
        @PrimaryKey
        open var id: String = "",
        open var serviceName: String = "",
        open var number: String = "",
        open var installPlace: String = "",
        open var date: Date? = null,
        open var period: Date? = null,
        open var isSent: Boolean = false,
        open var value: String = "") : RealmObject()