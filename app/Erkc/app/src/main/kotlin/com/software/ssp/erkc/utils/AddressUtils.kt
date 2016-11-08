package com.software.ssp.erkc.utils


fun splitFullAddress(address: String): List<String>{
    val mutableList = mutableListOf<String>()

    val HOUSE_DELIMITER = " д."
    val APARTMENT_DELIMITER = " кв."

    val addressSplits = address.split(HOUSE_DELIMITER)
    mutableList.add(addressSplits[0])
    if(addressSplits.size>1) {
        val houseAndApartmentSplits = addressSplits[1].split(APARTMENT_DELIMITER)
        mutableList.add(houseAndApartmentSplits[0])
        if (houseAndApartmentSplits.size > 1) {
            mutableList.add(houseAndApartmentSplits[1])
        }
    }

    return mutableList.toList()
}

fun getStreetFromShortAddress(address: String): String{
    val DELIMITER = " "
    val DIGIT_REGEX = Regex("[0-9]+")
    return address.split(DELIMITER)
            .dropLastWhile { it.length < 2 || it.contains(DIGIT_REGEX)}
            .joinToString(DELIMITER)
}