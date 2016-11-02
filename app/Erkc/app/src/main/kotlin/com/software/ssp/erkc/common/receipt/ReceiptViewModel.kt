package com.software.ssp.erkc.common.receipt

class ReceiptViewModel (
        var id: String?,
        var name: String,
        var address: String,
        var barcode: String,
        var amount: String,
        var type: ReceiptType,
        var isAutoPayment: Boolean,
        var isCardLinked: Boolean,
        var lastPayDate: String?,
        var lastValueTransferDate: String?
)

enum class ReceiptType{
    WATER,
    POWER
}