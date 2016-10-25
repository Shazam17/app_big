package com.software.ssp.erkc.data.rest

import com.software.ssp.erkc.data.rest.models.User


class ActiveSession {

    var appToken: String? = null
    var accessToken: String? = null
    var user: User? = null

    fun clear() {
        accessToken = null
        user = null
    }
}