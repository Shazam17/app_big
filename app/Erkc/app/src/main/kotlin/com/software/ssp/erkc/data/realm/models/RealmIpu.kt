package com.software.ssp.erkc.data.realm.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class RealmIpu(
        @PrimaryKey
        open var receiptId: String = "",
        open var ipuValues: RealmList<RealmIpuValue> = RealmList(),
        open var receipt: RealmReceipt? = null) : RealmObject()