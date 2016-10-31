package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Alexander Popov on 25/10/2016.
 */
data class Address(
        @SerializedName("id")
        val id: Long,
        @SerializedName("name")
        val name: String,
        @SerializedName("query")
        val query: String
)

open class AddressCache(
        @PrimaryKey
        open var id: Long = 0,

        open var name: String = "",

        open var query: String = ""
) : RealmObject() {
}

