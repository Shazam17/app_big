package com.software.ssp.erkc.data.rest


class ActiveSession {

    var appToken: String? = null
    var flag:Int?=-1
    var accessToken: String? = null
    var userAccessToken:String? = null
    var isOfflineSession: Boolean = false

    fun clear() {
        accessToken = null
        isOfflineSession = false
    }
}
