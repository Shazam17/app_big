package com.software.ssp.erkc.modules.history.filter

import com.software.ssp.erkc.data.realm.models.ReceiptType
import com.software.ssp.erkc.data.rest.models.PaymentMethod
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import java.util.*

@PaperParcel
data class HistoryFilterModel(
        var barcode: String = "",
        var street: String = "",
        var house: String = "",
        var apartment: String = "",
        var periodFrom: Date? = null,
        var periodTo: Date? = null,

        var paymentSum: Double? = null,
        var paymentType: ReceiptType? = null,
        var paymentMethod: PaymentMethod? = null,

        var deviceNumber: String = "",
        var deviceInstallPlace: String = "") : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(HistoryFilterModel::class.java)
    }
}

enum class HistoryFilterField {
    BARCODE,
    STREET,
    HOUSE,
    APARTMENT,
    PERIOD,
    MAX_SUM,
    PAYMENT_TYPE,
    PAYMENT_METHOD,
    DEVICE_NUMBER,
    DEVICE_PLACE
}