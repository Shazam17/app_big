package com.software.ssp.erkc.common


class ApiException(
        message: String,
        val errorCode: Int
) : Exception(message) {
}