package com.software.ssp.erkc.extensions

import okhttp3.FormBody


fun FormBody.getParameters(): MutableMap<String, String> {
    if (size() <= 0) {
        return mutableMapOf()
    }
    val maxN = size() - 1
    val params = mutableMapOf<String, String>()
    for (i in 0..maxN) {
        params.put(name(i), value(i))
    }
    return params
}