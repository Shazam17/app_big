package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName
import com.software.ssp.erkc.data.realm.models.RealmPhotoPath
import com.software.ssp.erkc.data.realm.models.RealmRequestStatus
import io.realm.RealmList

class Request (
        @SerializedName("id")
        val id:Int,
        @SerializedName("title")
        val title:String?,
        @SerializedName("type")
        val type:String?,
        @SerializedName("typeTab")
        var typeTab: String?,
        @SerializedName("date")
        var date: String?,
        @SerializedName("number")
        var number: Int?,
        @SerializedName("countMessages")
        var countMessages: Int?,
        @SerializedName("description")
        var description: String?,
        @SerializedName("infoAboutProblem")
        var infoAboutProblem: String?,
        @SerializedName("status")
        var status: List<RequestStatus>?,
        @SerializedName("photosPath")
        var photosPath: List<String>?,
        @SerializedName("isCrash")
        var isCrash: Boolean?,
        @SerializedName("nameManagerCompany")
        var nameManagerCompany: String?,
        @SerializedName("typeStore")
        var typeStore: String?,
        @SerializedName("serviceProvider")
        var serviceProvider: String?,
        @SerializedName("address")
        var address: String?,
        @SerializedName("numberPhone")
        var numberPhone: String?,
        @SerializedName("FIO")
        var FIO: String?
)

class RequestStatus (
        @SerializedName("type")
        var type: String?,
        @SerializedName("date")
        var date: String?
)