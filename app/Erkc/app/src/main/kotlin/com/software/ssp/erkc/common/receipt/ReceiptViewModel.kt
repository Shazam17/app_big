package com.software.ssp.erkc.common.receipt

import com.software.ssp.erkc.data.rest.models.Receipt


class ReceiptViewModel (
        val receipt: Receipt,
        var isRemovePending: Boolean
)
