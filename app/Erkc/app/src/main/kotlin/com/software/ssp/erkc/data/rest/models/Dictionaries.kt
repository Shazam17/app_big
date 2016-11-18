package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

/**
 * @author Alexander Popov on 25/10/2016.
 */
data class Address(
        @SerializedName("id")
        val id: Long,
        @SerializedName("name")
        val name: String
)

data class Streets(
        @SerializedName("street")
        val street: Array<String>)