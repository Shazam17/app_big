package com.software.ssp.erkc.modules.history.filter

import com.plumillonforge.android.chipview.Chip


class FilterChipTag(val value: String, val field: HistoryFilterField) : Chip {
    override fun getText(): String {
        return value
    }
}
