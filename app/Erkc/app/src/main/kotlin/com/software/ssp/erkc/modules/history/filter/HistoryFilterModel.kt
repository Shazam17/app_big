package com.software.ssp.erkc.modules.history.filter

import java.util.*


data class HistoryFilterModel(
        var barcode: String = "",
        var street: String = "",
        var house: String = "",
        var apartment: String = "",
        var periodFrom: Date? = null,
        var periodTo: Date? = null,

        var paymentSum: String = "",
        var paymentType: String = "",
        var paymentProcess: String = "")