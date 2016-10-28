package com.software.ssp.erkc.extensions

import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.widget.TextView
import org.jetbrains.anko.textColor

/**
 * @author Alexander Popov on 28/10/2016.
 */
fun TextView.setTextColorByContextCompat(@ColorInt colorInt: Int) {
    this.textColor = ContextCompat.getColor(rootView.context, colorInt)
}