package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.data.realm.models.RealmReceipt
import com.software.ssp.erkc.data.rest.models.Receipt


fun RealmReceipt.toReceipt() : Receipt {
    return Receipt(
            this.street,
            this.house,
            this.apart,
            this.autoPayMode,
            this.name,
            this.maxSum,
            this.id,
            this.lastPayment,
            this.address,
            this.serviceCode,
            this.amount,
            this.barcode,
            this.lastValueTransfer,
            this.supplierName,
            this.percent,
            this.linkedCard?.id)
}
