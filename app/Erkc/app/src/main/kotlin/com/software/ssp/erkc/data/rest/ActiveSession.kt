package com.software.ssp.erkc.data.rest

import android.content.Context
import com.securepreferences.SecurePreferences


class ActiveSession {

    var appToken: String? = null

    var accessToken: String? = null
    var isOfflineSession: Boolean = false

    fun clear() {
        accessToken = null
        isOfflineSession = false
    }
}
