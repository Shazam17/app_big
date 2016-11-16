package com.software.ssp.erkc.extensions

import android.util.Patterns
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest

fun String.isEmail(): Boolean {
    return matches(Patterns.EMAIL_ADDRESS.toRegex())
}

fun String.md5(): String {
    val digester = MessageDigest.getInstance("MD5")
    digester.update(this.toByteArray(Charset.defaultCharset()))
    val digest = digester.digest()

    val bigInt = BigInteger(1, digest)
    var md5Hex = bigInt.toString(16)

    while (md5Hex.length < 32) {
        md5Hex = "0" + md5Hex
    }

    return md5Hex
}