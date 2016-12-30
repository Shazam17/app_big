package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


open class RealmPaymentInfo(
        @PrimaryKey
        open var id: String = "",
        open var date: Date? = null,
        open var house: String = "",
        open var status: Int = 0,
        open var street: String = "",
        open var barcode: String = "",
        open var operationId: String = "",
        open var modeId: Int = 0,
        open var sum: Double = 0.0,
        open var supplierName: String = "",
        open var serviceName: String = "",
        open var amount: Double = 0.0,
        open var text: String = "",
        open var address: String = "",
        open var apart: String = "",
        open var checkFile: String? = null,
        open var errorDesc: String? = null,
        open var receipt: RealmReceipt? = null,
        open var paymentCard: RealmCard? = null) : RealmObject()