package com.software.ssp.erkc.modules.notifications.filter

import com.software.ssp.erkc.modules.history.filter.HistoryFilterModel
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import java.util.*

@PaperParcel
data class NotificationsFilterModel(
        var title: String = "",
        var message: String = "",
        var periodFrom: Date? = null,
        var periodTo: Date? = null,
        var isRead: Boolean = false) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(HistoryFilterModel::class.java)
    }
}

enum class NotificationsFilterField {
    TITLE,
    MESSAGE,
    PERIOD,
    READ
}