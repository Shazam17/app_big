package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class RequestAdress (
        @SerializedName("id")
        var id:Int,
        @SerializedName("company_id")
        var company_id:Int?,
        @SerializedName("code")
        var code:String?,
        @SerializedName("address")
        var address:String?,
        @SerializedName("fias")
        var fias:String?,
        @SerializedName("cadastral_number")
        var cadastral_number:String?
)