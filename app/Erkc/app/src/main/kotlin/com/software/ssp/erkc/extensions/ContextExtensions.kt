package com.software.ssp.erkc.extensions

import android.app.Service
import android.content.Context
import android.provider.Settings
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.intentFor

fun Context.getCompatColor(colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}

val Context.deviceId: String
    get() {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

inline fun <reified T : Service> Context.stopService(vararg params: Pair<String, Any>) {
    stopService(intentFor<T>())
}