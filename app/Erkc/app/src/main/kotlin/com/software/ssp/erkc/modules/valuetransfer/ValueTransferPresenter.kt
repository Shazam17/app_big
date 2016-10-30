package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.rest.models.Receipt
import java.util.*
import javax.inject.Inject

class ValueTransferPresenter @Inject constructor(view: IValueTransferView) : RxPresenter<IValueTransferView>(view), IValueTransferPresenter {

    override fun onViewAttached() {
        super.onViewAttached()

        val receiptsList = getMockReceiptList().sortedBy(Receipt::address)

        val receiptsViewModels = ArrayList<ReceiptsViewModel>()

        receiptsList.forEach {
            if(it.address != receiptsViewModels.lastOrNull()?.headerTitle)
            {
                receiptsViewModels.add(
                        ReceiptsViewModel(it.address, ArrayList<Receipt>())
                )
            }
            receiptsViewModels.last().itemsInSection.add(it)
        }

        view?.showData(receiptsViewModels)
    }

    override fun onTransferValueClick(receipt: Receipt) {
        view?.navigateToSendValues(receipt)
    }

    override fun onSwipeToRefresh() {

    }

    override fun onItemClick(item: ReceiptsViewModel) {

    }

    private fun getMockReceiptList(): List<Receipt> {
        return listOf(
                Receipt("Иван", "-569 р.", "Оплата ХВ", "пр. Ленина 34, кв.45", "2346784567890"),
                Receipt("Иван", "-11 р.", "Электроэнергия", "пр. Ленина 34, кв.45", "2346784567891"),
                Receipt("Иван", "-234 р.", "Электроэнергия", "ул. Сибирская 12, кв.1", "2346784567892"),
                Receipt("Иван", "0.00 р.", "Оплата ХВ", "ул. Сибирская 12, кв.1", "2346784567888"),
                Receipt("Иван", "-569 р.", "Отопление и ГС", "пер. Дербышевского 1, кв.21", "2346784567893"),
                Receipt("Иван", "-1236.45 р.", "Электроэнергия", "пер. Дербышевского 1, кв.21", "2346784567823"),
                Receipt("Иван", "-569.1 р.", "Оплата ХВ", "пер. Дербышевского 1, кв.21", "2346784567893"),
                Receipt("Иван", "-232 р.", "Оплата ХВ", "пр. Ленин 34, кв.45", "2346784567894"),
                Receipt("Иван", "-569 р.", "Оплата ХВ", "пр. Ленина 34, кв.45", "2346784567895"),
                Receipt("Иван", "-11 р.", "Электроэнергия", "пр. Ленина 34, кв.45", "2346784567896"),
                Receipt("Иван", "-234 р.", "Электроэнергия", "ул. Сибирская 12, кв.1", "2346784567897"),
                Receipt("Иван", "0.00 р.", "Оплата ХВ", "ул. Сибирская 12, кв.1", "2346784567889"),
                Receipt("Иван", "-569 р.", "Отопление и ГС", "пер. Дербышевского 1, кв.21", "2346784567894"),
                Receipt("Иван", "-1236.45 р.", "Электроэнергия", "пер. Дербышевского 1, кв.21", "2346784567824"),
                Receipt("Иван", "-569.1 р.", "Оплата ХВ", "пер. Дербышевского 1, кв.21", "2346784567895"),
                Receipt("Иван", "-232 р.", "Оплата ХВ", "пр. Ленин 34, кв.45", "2346784567898")
        )
    }
}