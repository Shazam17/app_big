package com.software.ssp.erkc

import com.chibatching.kotpref.KotprefModel

/**
 * @author Alexander Popov on 01/11/2016.
 */
object AppPrefs : KotprefModel() {
    var lastCashingDate: Long by longPrefVar(default = -1L)
}