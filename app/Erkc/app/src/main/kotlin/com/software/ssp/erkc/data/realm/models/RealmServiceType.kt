package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject


open class RealmServiceType(
        open var id: String = "",
        open var name: String = "",
        open var service_code: String = "",
        open var icon: ByteArray? = null) : RealmObject()