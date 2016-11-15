package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

/**
 * @author Alexander Popov on 28/10/2016.
 */
@PaperParcel
data class Card(
        @SerializedName("id")
        var id: String,
        @SerializedName("name")
        var name: String,
        @SerializedName("status_id")
        val statusId: Int,
        @SerializedName("remotecardid")
        var remoteCardId: String? = null,
        @SerializedName("maskcardno")
        var maskCardNo: String? = null,
        @SerializedName("statusstr")
        var statusStr: String
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Card::class.java)
    }
}

data class CardRegistration(
        @SerializedName("id")
        var id: String,
        @SerializedName("url")
        var url: String
)
data class CardActivation(
        @SerializedName("id")
        var id: String,
        @SerializedName("url")
        var url: String
)