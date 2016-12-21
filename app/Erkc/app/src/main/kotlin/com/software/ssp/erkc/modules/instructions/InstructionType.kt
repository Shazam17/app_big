package com.software.ssp.erkc.modules.instructions

import android.support.annotation.StringRes
import com.software.ssp.erkc.R


enum class InstructionType(@StringRes val titleId: Int) {
    NON_AUTH_SEND_VALUES(R.string.instruction_send_values_non_auth_title),
    NON_AUTH_TITLE(R.string.instruction_payment_non_auth_title),
    PAYMENT_ADD_RECEIPT(R.string.instruction_payment_add_receipt_title),
    SEND_VALUES_ADD_RECEIPT(R.string.instruction_send_values_add_receipt_title),
    SEND_VALUES(R.string.instruction_send_values_title),
    PAYMENT(R.string.instruction_payment_title),
    PAYMENT_WITH_CARD(R.string.instruction_payments_with_cards_title),
    CARDS(R.string.instruction_cards_title),
    AUTO_PAYMENTS(R.string.instruction_auto_payments_title),
    HISTORY_PAYMENT(R.string.instruction_history_payment_title),
    HISTORY_SEND_VALUES(R.string.instruction_history_send_value_title),
    NOTIFICATIONS(R.string.instruction_notifications_title)
}
