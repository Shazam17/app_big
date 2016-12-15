package com.software.ssp.erkc.extensions

import java.text.SimpleDateFormat
import java.util.*


fun Date.toString(format: String): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}