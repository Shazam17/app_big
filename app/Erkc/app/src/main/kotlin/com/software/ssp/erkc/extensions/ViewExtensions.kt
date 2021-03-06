package com.software.ssp.erkc.extensions

import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.view.View
import org.jetbrains.anko.backgroundColor

/**
 * @author Alexander Popov on 28/10/2016.
 */
fun View.setBackgroundColorByContextCompat(@ColorInt colorInt: Int) {
    this.backgroundColor = ContextCompat.getColor(rootView.context, colorInt)
}

fun View.hide() {
    this.alpha = 0f
    this.isClickable = false
}
fun View.show() {
    this.alpha = 1f
    this.isClickable = true
}
