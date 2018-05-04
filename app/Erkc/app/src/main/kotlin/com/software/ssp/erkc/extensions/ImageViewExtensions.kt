package com.software.ssp.erkc.extensions

import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.realm.models.RealmReceipt

/**
 * @author Alexander Popov on 31/10/2016.
 */
fun ImageView.load(byteArray: ByteArray) {
    setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
}

fun ImageView.setIcon(receipt: RealmReceipt) {
    val path = receipt.iconPath(context)
    if (path != null) {
        Glide.with(context)
                .load(path)
                .into(this)
    } else {
        //setImageBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.ic_circle_warning))
        setImageBitmap(null)
    }
}