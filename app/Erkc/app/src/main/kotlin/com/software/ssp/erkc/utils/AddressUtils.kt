package com.software.ssp.erkc.utils

fun getStreetFromShortAddress(address: String): String{
    val DELIMITER = " "
    val DIGIT_REGEX = Regex("[0-9]+")
    return address.split(DELIMITER)
            .dropLastWhile { it.length < 2 || it.contains(DIGIT_REGEX)}
            .joinToString(DELIMITER)
}


