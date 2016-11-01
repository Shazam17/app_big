package com.software.ssp.erkc.extensions

import android.util.Patterns

fun String.isEmail(): Boolean {
    return matches(Patterns.EMAIL_ADDRESS.toRegex())
}

fun String.getHouse(): String {
    val lastChar = this.trimEnd().last()
    var houseNo : String
    if (lastChar.isDigit()) {
        houseNo = this.trim().split(" ").last()
    } else {
        val split = this.trim().split(" ")
        houseNo = "${split[split.count() - 2]} ${split[split.count() - 1]}"
    }
    return houseNo
}

fun String.getStreet(): String {
    val lastChar = this.trimEnd().last()
    var street : String
    var houseNo : String
    if (lastChar.isDigit()) {
        houseNo = this.trim().split(" ").last()
        street = this.replace(houseNo, "")
    } else {
        val split = this.trim().split(" ")
        houseNo = "${split[split.count() - 2]} ${split[split.count() - 1]}"
        street = this.replace(houseNo, "")
    }
    return street
}