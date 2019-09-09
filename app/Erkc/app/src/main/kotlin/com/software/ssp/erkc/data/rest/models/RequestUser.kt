package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class RequestUser(
        @SerializedName("company_id")
        val company_id:Int,
        @SerializedName("house_id")
        val house_id:Int,
        @SerializedName("premise_id")
        val premise_id:Int,
        @SerializedName("last_name")
        val last_name:String,
        @SerializedName("first_name")
        val first_name:String,
        @SerializedName("middle_name")
        val middle_name:String,
        @SerializedName("access_token")
        val access_token:String,
        @SerializedName("code")
        val code:String,
        @SerializedName("id")
        val id:Int
)