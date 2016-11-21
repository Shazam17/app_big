package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

data class ApiError(
        @SerializedName("desc")
        val description: String,
        @SerializedName("code")
        val code: Int)


object ApiErrorType{
        const val INVALID_REQUEST = 100
        const val UNAUTHORIZED_USER_FAILED = 110
        const val USER_AUTHENTICATION_FAILED = 111
        const val UNAUTHORIZED_APPLICATION = 112

        const val NOT_SET_BARCODE = 314
        const val NOT_SET_STREET = 315
        const val NOT_SET_HOUSE = 316

        const val UNKNOWN_BARCODE = 318

        const val PAYMENT_ERROR = 309
}