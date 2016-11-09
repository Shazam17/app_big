package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName


class User(
        @SerializedName("email")
        val email: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("address")
        val address: String?, //Maybe removed from API model
        @SerializedName("login")
        val login: String)
