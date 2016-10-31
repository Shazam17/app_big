package com.software.ssp.erkc.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * @author Alexander Popov on 31/10/2016.
 */
fun ImageView.load(byteArray: ByteArray) {
    Glide.with(context)
            .load(byteArray)
            .into(this)
}
