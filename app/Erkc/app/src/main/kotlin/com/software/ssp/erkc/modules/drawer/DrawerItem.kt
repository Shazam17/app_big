package com.software.ssp.erkc.modules.drawer

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import com.software.ssp.erkc.R

enum class DrawerItem(@StringRes val titleId: Int, @IdRes val itemId: Int) {
    MAIN(R.string.drawer_main_screen_text, R.id.menuMainScreen),
    PAYMENT(R.string.drawer_payment_text, R.id.menuPayment),
    VALUES(R.string.drawer_send_values_text, R.id.menuSendValues),
    CARDS(R.string.drawer_my_cards_text, R.id.menuMyCards),
    HISTORY(R.string.drawer_history_text, R.id.menuHistory),
    AUTOPAY(R.string.drawer_auto_payment_text, R.id.menuAutoPayments),
    NOTIFY(R.string.drawer_notifications_text, R.id.menuNotifications),
    SETTINGS(R.string.drawer_settings_text, R.id.menuSettings),
    TUTORIAL(R.string.drawer_instruction_text, R.id.menuInstructions),
    CONTACTS(R.string.drawer_contacts_text, R.id.menuContacts),
    EXIT(R.string.drawer_exit_text, R.id.menuExit)
}