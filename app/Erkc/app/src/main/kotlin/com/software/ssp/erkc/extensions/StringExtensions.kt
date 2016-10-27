package com.software.ssp.erkc.extensions

import android.util.Patterns

fun String.isEmail(): Boolean {
    return matches(Patterns.EMAIL_ADDRESS.toRegex())
}