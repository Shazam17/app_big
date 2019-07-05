package com.software.ssp.erkc.data.realm.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmRequest(
        @PrimaryKey
        open var id:Int=0,
        open var state:String?="",
        open var title:String?="",
        open var type:String?="",
        open var typeTab: String? = "",
        open var date: String? = "",
        open var number: Int? = null,
        open var countMessages: Int? = null,
        open var description: String? = "",
        open var infoAboutProblem: String? = "",
        open var status: RealmList<RealmRequestStatus>? = null,
        open var photosPath: RealmList<RealmPhotoPath>? = null,
        open var messages: RealmList<RealmChatMessage>? = null,
        open var isCrash: Boolean? = false,
        open var nameManagerCompany: String? = "",
        open var typeStore: String? = "",
        open var serviceProvider: String? = "",
        open var address: String? = "",
        open var numberPhone: String? = "",
        open var FIO: String? = ""
) : RealmObject()