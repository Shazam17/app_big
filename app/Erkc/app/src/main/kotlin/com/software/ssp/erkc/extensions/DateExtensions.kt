package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.Constants
import java.text.SimpleDateFormat
import java.util.*


val Date.historyFormat: String
    get() = SimpleDateFormat(Constants.HISTORY_DATE_FORMAT, Locale.getDefault()).format(this)

val Date.receiptFormat: String
    get() = SimpleDateFormat(Constants.RECEIPT_DATE_FORMAT, Locale.getDefault()).format(this)

val Date.ipuValuesFormat: String
    get() = SimpleDateFormat(Constants.VALUES_DATE_FORMAT, Locale.getDefault()).format(this)