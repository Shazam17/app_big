package com.software.ssp.erkc.data.rest


class ActiveSession {

    var accessToken: String? = null

    fun clear() {
        accessToken = null
    }
}