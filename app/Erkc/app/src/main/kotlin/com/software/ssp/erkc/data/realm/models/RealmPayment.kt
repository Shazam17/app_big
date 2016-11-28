package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmPayment(
        @PrimaryKey
        open var id: String = "",
        open var date: String = "",
        open var amount: Double = 0.0,
        open var checkFile: String = "",
        open var status: Int = 0,
        open var maskedCardNumber: String = "",
        open var comment: String = "",
        open var errorCode: String = "",
        open var errorDesc: String = "",
        open var methodId: String = "",
        open var receipt: RealmReceipt? = null) : RealmObject()