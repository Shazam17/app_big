package com.software.ssp.erkc.modules.valuetransfer.valuetrasferlist

class ReceiptViewModel (
        var id: String,
        var name: String,
        var address: String,
        var barcode: String,
        var amount: String,
        var type: ReceiptType,
        var isAutoPayment: Boolean,
        var isCardLinked: Boolean,
        var lastPayDate: String?,
        var lastValueTransferDate: String?,
        var screen: ReceiptScreen
)

enum class ReceiptType{
    WATER,
    POWER
}

enum class ReceiptScreen{
    MAIN,
    PAYMENT,
    VALUE_TRANSFER
}