package com.software.ssp.erkc.extensions

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView


fun ImageView.setBase64Bitmap(base64Source: String){
    val decodedBytes = Base64.decode(base64Source,0)
    val bitmap = BitmapFactory.decodeByteArray(decodedBytes,0, decodedBytes.size)
    bitmap.let {
        setImageBitmap(it)
    }
}