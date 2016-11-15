package com.software.ssp.erkc.extensions

import android.support.annotation.StringRes
import com.software.ssp.erkc.R

/**
 * @author Alexander Popov on 31/10/2016.
 */
enum class CardStatus(@StringRes val stringResId: Int) {
    NONE(R.string.none),
    NOT_REGISTERED(R.string.card_status_not_registered),
    REGISTERED(R.string.card_status_registered),
    ACTIVATED(R.string.card_status_activate),
    DELETED(R.string.card_status_deleted);
}

fun CardStatus.backgroundColor(): Int {
    if (this == CardStatus.ACTIVATED) {
        return R.color.colorCardBackActivated
    } else {
        return R.color.colorCardBackNotActivated
    }
}

fun CardStatus.nameColor(): Int {
    if (this == CardStatus.ACTIVATED) {
        return R.color.colorCardNameActivated
    } else {
        return R.color.colorCardNameNotActivated
    }
}

fun CardStatus.dividerColor(): Int {
    if (this == CardStatus.ACTIVATED) {
        return R.color.colorCardDividerActivated
    } else {
        return R.color.colorCardDividerNotActivated
    }
}
