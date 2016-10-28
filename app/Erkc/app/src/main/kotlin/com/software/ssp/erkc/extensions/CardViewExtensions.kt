package com.software.ssp.erkc.extensions

import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.widget.TextView
import org.jetbrains.anko.textColor

/**
 * @author Alexander Popov on 28/10/2016.
 */
fun CardView.setCardBackgroundColorByContextCompat(@ColorInt colorInt: Int) {
    this.setCardBackgroundColor(ContextCompat.getColor(rootView.context, colorInt))
}