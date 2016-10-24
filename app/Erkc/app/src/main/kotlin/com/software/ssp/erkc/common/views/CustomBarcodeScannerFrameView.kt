package com.software.ssp.erkc.common.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import me.dm7.barcodescanner.core.ViewFinderView

class CustomBarcodeScannerFrameView : ViewFinderView {
    var finderRect: Rect? = null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    override fun onDraw(canvas: Canvas) {
        framingRect.set(finderRect)
        drawViewFinderMask(canvas)
    }
}