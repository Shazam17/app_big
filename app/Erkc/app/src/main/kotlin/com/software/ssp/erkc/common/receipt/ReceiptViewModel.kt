package com.software.ssp.erkc.common.receipt

import com.software.ssp.erkc.data.realm.models.RealmReceipt


class ReceiptViewModel(
        val receipt: RealmReceipt,
        var isRemovePending: Boolean = false
)
