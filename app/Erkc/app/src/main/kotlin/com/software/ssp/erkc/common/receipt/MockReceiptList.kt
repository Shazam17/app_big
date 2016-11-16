package com.software.ssp.erkc.common.receipt

import com.software.ssp.erkc.data.rest.models.Receipt

fun getMockReceiptList(): List<Receipt> {
    return listOf(
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина д.10  кв.45", "0", "2346784567890", "1"),
            Receipt(0.0, "0", "Иван", "Оплата ГВ", "пр. Ленина д.20  кв.46", "1", "2346784567891", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина д.30  кв.45", "2", "2346784567892", "1"),
            Receipt(0.0, "0", "Иван", "Оплата ГВ", "пр. Ленина  д.30 кв.46", "3", "2346784567893", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина   д.40  кв.45", "4", "2346784567894", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина   д.11  кв.45", "5", "2346784567895", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата Электричества", "пр. Ленина   д.30  кв.46", "6", "2346784567896", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.45", "7", "2346784567897", "1"),
            Receipt(0.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.45", "8", "2346784567898", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.45", "9", "2346784567899", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ГВ", "пр. Ленина  д.30 кв.47", "10", "2346784567900", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.45", "11", "2346784567901", "1"),
            Receipt(0.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.47", "12", "2346784567902", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата Электричества", "пр. Ленина  д.30 кв.45", "13", "2346784567903", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.45", "14", "2346784567904", "1"),
            Receipt(0.0, "0", "Иван", "Оплата ГВ", "пр. Ленина  д.30 кв.48", "15", "2346784567905", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.45", "16", "2346784567906", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата Электричества", "пр. Ленина  д.30 кв.45", "17", "2346784567907", "1"),
            Receipt(0.0, "0", "Иван", "Оплата ХВ", "пр. Ленина  д.30 кв.48", "18", "2346784567908", "1"),
            Receipt(1337.0, "0", "Иван", "Оплата ГВ", "пр. Ленина  д.30 кв.45", "19", "2346784567909", "1")
    )
}
