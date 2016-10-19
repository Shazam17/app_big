package com.software.ssp.erkc.extensions

import android.content.Context
import android.support.v4.content.ContextCompat

fun Context.getCompatColor(colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}