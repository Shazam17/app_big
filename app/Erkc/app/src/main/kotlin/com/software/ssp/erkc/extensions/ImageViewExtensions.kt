package com.software.ssp.erkc.extensions

import android.graphics.BitmapFactory
import android.widget.ImageView

/**
 * @author Alexander Popov on 31/10/2016.
 */
fun ImageView.load(byteArray: ByteArray) {
    setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
}
