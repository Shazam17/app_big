package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.R
import com.software.ssp.erkc.data.rest.models.PaymentMethod

fun PaymentMethod.getStringResId(): Int {
    when (this) {
        PaymentMethod.DEFAULT -> return R.string.payment_default
        PaymentMethod.ONE_CLICK -> return R.string.payment_one_click
        PaymentMethod.AUTO -> return R.string.payment_auto
    }
}
