package com.software.ssp.erkc.modules.mainscreen.receiptlist

import android.support.annotation.IdRes
import com.software.ssp.erkc.R

enum class ReceiptMenuItem(@IdRes val itemId: Int) {
    HISTORY(R.id.menuHistory),
    AUTOPAY(R.id.menuAutoPay)
}