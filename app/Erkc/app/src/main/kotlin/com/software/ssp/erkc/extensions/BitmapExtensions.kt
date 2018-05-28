package com.software.ssp.erkc.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import timber.log.Timber

fun Bitmap.rotate90CW(): Bitmap {
    val res = Bitmap.createBitmap(this.height, this.width, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(res)
    canvas.rotate(90f)
    canvas.drawBitmap(this, 0f, -this.height.toFloat(), null)
//CCW:
//        canvas.rotate(-90f)
//        canvas.drawBitmap(this, -this.width.toFloat(), 0f, null)
    return res
}

fun Bitmap.resize(min_dim: Int): Bitmap {
    var w = this.width.toFloat()
    var h = this.height.toFloat()
    val factor = Math.min(w,h)/min_dim.toFloat()
    w /= factor
    h /= factor

    return Bitmap.createScaledBitmap(this, w.toInt(), h.toInt(), true)
}

fun Bitmap.crop(width: Int, height: Int): Bitmap {
    this.let {
        val w1 = width
        val h1 = height
        val h = it.height
        val w = it.width
        var h2 = h1
        var w2 = w1
        Timber.d("cropping ${w}x${h} to ${w1}x${h1}")

        val scale_w = w/w2.toFloat()
        val scale_h = h/h2.toFloat()
        if (scale_w < scale_h) {
            w2 = w
            h2 = (h/scale_w).toInt()
        } else {
            h2 = h
            w2 = (w/scale_h).toInt()
        }

        if (w1 > w && h1 < h) {
            val factor: Float = w1/w.toFloat()
            w2 = w
            h2 = (h1/factor).toInt()
        }
        if (h1 > h && w1 < w) {
            val factor: Float = h1/h.toFloat()
            h2 = h
            w2 = (w1/factor).toInt()
        }

        if (w2 < w && h2 < h) {
            return Bitmap.createBitmap(it,
                    (w-w2)/2, (h-h2)/2,
                    w2, h2
            )
        } else if (w2 < w) {
            return Bitmap.createBitmap(it,
                    (w-w2)/2, 0,
                    w2, h)
        } else if (h2 < h) {
            return Bitmap.createBitmap(it,
                    0, (h-h2)/2,
                    w, h2)
        }
    }
    return this
}
