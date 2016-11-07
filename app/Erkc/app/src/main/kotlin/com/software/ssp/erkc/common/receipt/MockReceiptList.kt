package com.software.ssp.erkc.common.receipt

import com.software.ssp.erkc.data.rest.models.Receipt

fun getMockReceiptList(): List<Receipt> {
    return listOf(
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "0", "2346784567890"),
            Receipt("0 р.", "0", "Иван", "Оплата ГВ", "пр. Ленина 34, кв.46", "1", "2346784567891"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "2", "2346784567892"),
            Receipt("0 р.", "0", "Иван", "Оплата ГВ", "пр. Ленина 34, кв.46", "3", "2346784567893"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "4", "2346784567894"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "5", "2346784567895"),
            Receipt("-1337 р.", "0", "Иван", "Оплата Электричества", "пр. Ленина 34, кв.46", "6", "2346784567896"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "7", "2346784567897"),
            Receipt("0 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "8", "2346784567898"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "9", "2346784567899"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ГВ", "пр. Ленина 34, кв.47", "10", "2346784567900"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "11", "2346784567901"),
            Receipt("0 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.47", "12", "2346784567902"),
            Receipt("-1337 р.", "0", "Иван", "Оплата Электричества", "пр. Ленина 34, кв.45", "13", "2346784567903"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "14", "2346784567904"),
            Receipt("0 р.", "0", "Иван", "Оплата ГВ", "пр. Ленина 34, кв.48", "15", "2346784567905"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.45", "16", "2346784567906"),
            Receipt("-1337 р.", "0", "Иван", "Оплата Электричества", "пр. Ленина 34, кв.45", "17", "2346784567907"),
            Receipt("0 р.", "0", "Иван", "Оплата ХВ", "пр. Ленина 34, кв.48", "18", "2346784567908"),
            Receipt("-1337 р.", "0", "Иван", "Оплата ГВ", "пр. Ленина 34, кв.45", "19", "2346784567909")
    )
}