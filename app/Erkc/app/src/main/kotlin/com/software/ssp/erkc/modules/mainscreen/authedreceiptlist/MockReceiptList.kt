package com.software.ssp.erkc.modules.mainscreen.authedreceiptlist

import com.software.ssp.erkc.data.rest.models.Receipt

fun getMockReceiptList(): List<Receipt> {
    return listOf<Receipt>(
            Receipt("Иван", "-569 р.", "Оплата ХВ", "пр. Ленина 34, кв.45", "2346784567890"),
            Receipt("Иван", "-11 р.", "Электроэнергия", "пр. Ленина 34, кв.45", "2346784567891"),
            Receipt("Иван", "-234 р.", "Электроэнергия", "ул. Сибирская 12, кв.1", "2346784567892"),
            Receipt("Иван", "0.00 р.", "Оплата ХВ", "ул. Сибирская 12, кв.1", "2346784567888"),
            Receipt("Иван", "-569 р.", "Отопление и ГС", "пер. Дербышевского 1, кв.21", "2346784567893"),
            Receipt("Иван", "-1236.45 р.", "Электроэнергия", "пер. Дербышевского 1, кв.21", "2346784567823"),
            Receipt("Иван", "-569.1 р.", "Оплата ХВ", "пер. Дербышевского 1, кв.21", "2346784567893"),
            Receipt("Иван", "-232 р.", "Оплата ХВ", "пр. Ленин 34, кв.45", "2346784567894")
    )
}
