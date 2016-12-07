package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RealmPayment(
        @PrimaryKey
        open var id: String = "",
        open var date: Date? = null,
        open var amount: Double = 0.0,
        open var checkFile: String = "",
        open var status: Int = 0,
        open var errorDesc: String? = null,
        open var maskedCardNumber: String = "",
        open var comment: String = "",
        open var errorCode: String = "",
        open var operationId: String = "",
        open var methodId: Int? = null,
        open var receipt: RealmReceipt? = null) : RealmObject()

open class RealmPaymentInfo(
        @PrimaryKey
        var id: String = "",
        var date: Date? = null,
        var house: String = "",
        var status: Int = 0,
        var street: String = "",
        var barcode: String = "",
        var operationId: String = "",
        var summ: Double = 0.0,
        var supplierName: String = "",
        var serviceName: String = "",
        var amount: Double = 0.0,
        var text: String = "",
        var address: String = "",
        open var receipt: RealmReceipt? = null,
        var apart: String = "") : RealmObject()

class PaymentAndPaymentInfo(
        val payment: RealmPayment,
        val paymentInfo: RealmPaymentInfo
)