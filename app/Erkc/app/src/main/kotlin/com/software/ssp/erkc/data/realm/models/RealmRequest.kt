package com.software.ssp.erkc.data.realm.models

import android.content.ClipDescription
import android.net.Uri
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmRequest(
        @PrimaryKey
        open var id: Int = 0,
        open var created_at: Long? = 0L,
        open var company: RealmCompany? = null,
        open var type: RealmTypeRequest? = null,
        open var state: RealmStateRequest? = null,
        open var applicant: String? = null,
        open var house: RealmHouseRequest? = null,
        open var premise: RealmPremiseRequest? = null,
        open var chanel: Int? = 0,
        open var chanelLabel: String? = null,
        open var name: String? = null,
        open var message: String? = "",
        open var contact: String? = null,
        open var code: String? = null,
        open var is_overdue: Int? = 0,
        open var messagePlane:String?=null,
//        open var task: RealmList<RealmRequestTask>? = RealmList(),
        open var comment: RealmList<RealmComment>? = RealmList(),
        open var isCrash: Boolean? = false,
        open var transitions:RealmList<RealmTransitions>?=RealmList()
) : RealmObject()

open class RealmTransitions(
        @PrimaryKey
        open var id:Int?=0,
        open var created_at: Long? = 0L,
        open var state: RealmStateRequest? = null
):RealmObject()

open class RealmComment(
        @PrimaryKey
        open var id: Int? = 0,
        open var created_at: Long? = 0L,
        open var initiator: Initiator? = null,
        open var message: String? = "",
        open var filename: String? = "",
        open var filetype: String? = "",
        open var downloadLink: String? = ""
) : RealmObject()

open class Initiator(
        @PrimaryKey
        open var id: Int? = 0,
        open var username: String? = "",
        open var name: String? = "",
        open var phone: String? = "",
        open var info: String? = ""
) : RealmObject()
//
//open class RealmRequestTask(
//        @PrimaryKey
//        open var id: Int? = 0
//) : RealmObject()

open class RealmCompany(
        @PrimaryKey
        open var id: Int? = 0,
        open var type: Int? = 0,
        open var typeLabel: String? = "",
        open var state: Int? = 0,
        open var stateLabel: String? = "",
        open var name: String? = "",
        open var full_name: String? = "",
        open var jur_address: String? = "",
        open var fact_address: String? = "",
        open var inn: String? = "",
        open var kpp: String? = "",
        open var ogrn: String? = "",
        open var email: String? = "",
        open var phone: String? = "",
        open var phone_number: String? = ""
) : RealmObject()

open class RealmTypeRequest(
        @PrimaryKey
        open var id: Int? = 0,
        open var name: String? = ""
) : RealmObject()

open class RealmStateRequest(
        @PrimaryKey
        open var id: Int? = 0,
        open var name: String? = "",
        open var stateLabel: String? = ""
) : RealmObject()

open class RealmHouseRequest(
        @PrimaryKey
        open var id: Int? = 0,
        open var company_id: Int? = 0,
        open var code: String? = "",
        open var address: String? = "",
        open var fias: String? = "",
        open var cadastral_number: String? = ""
) : RealmObject()

open class RealmPremiseRequest(
        @PrimaryKey
        open var id: Int? = 0,
        open var house_id: Int? = 0,
        open var number: String? = ""
) : RealmObject()

open class RealmTypeHouse(
        @PrimaryKey
        open var id: Int? = 0,
        open var name: String? = "",
        open var sort: Int? = 0
) : RealmObject()

open class RealmAddressRequest(
        @PrimaryKey
        var id: Int? = 0,
        var company_id: Int? = null,
        var code: String? = "",
        var address: String? = "",
        var fias: String? = "",
        var cadastral_number: String? = ""
) : RealmObject()

open class RealmDraft(
        @PrimaryKey
        var id: String = "",
        var title: String? = null,
        var address: String? = null,
        var company: String? = null,
        var typeRequest: String? = null,
        var typeHouse: String? = null,
        var description: String? = null,
        var fio: String? = null,
        var phoneNum: String? = null,
        var images: RealmList<RealmLocalImage> = RealmList()
) : RealmObject()

open class RealmLocalImage(
        var url: String? = null
) : RealmObject()