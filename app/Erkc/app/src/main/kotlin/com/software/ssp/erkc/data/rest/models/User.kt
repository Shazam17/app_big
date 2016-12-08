package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName


class User(
        @SerializedName("login")
        val login: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("name")
        val name: String
)

