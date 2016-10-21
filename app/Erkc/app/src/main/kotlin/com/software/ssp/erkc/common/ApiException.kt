package com.software.ssp.erkc.common


class ApiException(
        message: String,
        errorCode: Int
) : Exception(message) {
}