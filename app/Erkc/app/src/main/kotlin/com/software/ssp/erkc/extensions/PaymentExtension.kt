package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.data.realm.models.RealmPayment
import com.software.ssp.erkc.data.rest.models.PaymentMethod

/**
 * @author Alexander Popov on 06/12/2016.
 */
fun RealmPayment.type(): Int? {
    when (this.methodId) {
        PaymentMethod.DEFAULT.ordinal -> return PaymentMethod.DEFAULT.stringRes
        PaymentMethod.ONE_CLICK.ordinal -> return PaymentMethod.ONE_CLICK.stringRes
        PaymentMethod.AUTO.ordinal -> return PaymentMethod.AUTO.stringRes
        else -> return null
    }
}