package com.software.ssp.erkc.data.rest.models

import android.support.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.R

/**
 * @author Alexander Popov on 28/10/2016.
 */
data class Card(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("status_id")
        val statusId: Int,
        @SerializedName("remotecardid")
        var remoteCardId: String,
        @SerializedName("maskcardno")
        var maskCardNo: String,
        @SerializedName("statusstr")
        var statusStr: String

)

enum class CardStatus(@StringRes val stringResId: Int) {
    NONE(R.string.none),
    NOT_REGISTERED(R.string.card_status_not_registered),
    REGISTERED(R.string.card_status_registered),
    ACTIVATED(R.string.card_status_activate),
    DELETED(R.string.card_status_deleted);

}