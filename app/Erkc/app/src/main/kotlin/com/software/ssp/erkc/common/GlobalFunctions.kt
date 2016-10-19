package com.software.ssp.erkc.common

import com.software.ssp.erkc.BuildConfig

inline fun inDebugMode(block: (() -> Unit)) {
    if (BuildConfig.DEBUG) {
        block.invoke()
    }
}
