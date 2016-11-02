package com.software.ssp.erkc.extensions

import android.util.Patterns
import com.software.ssp.erkc.Constants
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser

fun String.isEmail(): Boolean {
    return matches(Patterns.EMAIL_ADDRESS.toRegex())
}

fun String.toBarcodeFormat(): String {
    val slots = UnderscoreDigitSlotsParser().parseSlots(Constants.BARCODE_FORMAT)
    val mask = MaskImpl.createTerminated(slots)
    mask.insertFront(this)
    return mask.toString()
}