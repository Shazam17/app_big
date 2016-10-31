package com.software.ssp.erkc.data.rest

import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.models.User


class ActiveSession {

    var appToken: String? = null
    var accessToken: String? = null
    var user: User? = null
    var cachedReceipts: List<Receipt>? = null

    fun clear() {
        accessToken = null
        user = null
        cachedReceipts = null
    }
}