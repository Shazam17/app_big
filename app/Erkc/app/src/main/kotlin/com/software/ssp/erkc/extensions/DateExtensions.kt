package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.Constants
import java.text.SimpleDateFormat
import java.util.*


fun Date.toString(format: String) : String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}

val Date.ipuValuesFormat: String
    get() = SimpleDateFormat(Constants.VALUES_DATE_FORMAT, Locale.getDefault()).format(this)
