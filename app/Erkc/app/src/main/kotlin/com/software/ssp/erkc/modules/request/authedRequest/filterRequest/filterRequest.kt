package com.software.ssp.erkc.modules.request.authedRequest.filterRequest

import com.plumillonforge.android.chipview.Chip

interface CheckedUser {
    var isChecked: Boolean
}

class StatusModel(
        override var isChecked: Boolean = false,
        val label: String
):CheckedUser

class FilterRequestChipTag(val field: String) : Chip {
    override fun getText(): String {
        return field
    }
}