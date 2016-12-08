package com.software.ssp.erkc.data.realm.models

import io.realm.RealmList
import io.realm.RealmObject


open class RealmIpu(
        open var ipuValues: RealmList<RealmIpuValue> = RealmList(),
        open var receipt: RealmReceipt? = null) : RealmObject()
