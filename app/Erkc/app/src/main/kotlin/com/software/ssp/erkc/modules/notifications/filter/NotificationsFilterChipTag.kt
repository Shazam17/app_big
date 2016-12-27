package com.software.ssp.erkc.modules.notifications.filter

import com.plumillonforge.android.chipview.Chip


class NotificationsFilterChipTag(val value: String, val field: NotificationsFilterField) : Chip {
    override fun getText(): String {
        return value
    }
}
