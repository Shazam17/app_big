package com.software.ssp.erkc.extensions

import java.util.*


fun Double.toStringWithDot() : String {
    return String.format(Locale.ENGLISH, "%.2f", this)
}