package com.software.ssp.erkc.data.rest.models

import com.google.gson.annotations.SerializedName

class Request (
        @SerializedName("id")
        val id:Int,
        @SerializedName("created_at")
        val created_at:Long?,
        @SerializedName("company")
        val company:Company?,
        @SerializedName("type")
        val type: TypeRequest?,
        @SerializedName("state")
        val state: StateRequest?,
        @SerializedName("applicant")
        val applicant: String?,
        @SerializedName("house")
        val house: HouseRequest?,
        @SerializedName("premise")
        val premise: PremiseRequest?,
        @SerializedName("chanel")
        val chanel: Int?,
        @SerializedName("chanelLabel")
        val chanelLabel: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("contact")
        val contact: String?,
        @SerializedName("code")
        val code: String?,
        @SerializedName("is_overdue")
        val is_overdue: Int?,
        @SerializedName("tasks")
        val tasks:List<Int>?,
        @SerializedName("comments")
        val comments:List<Comment>?
)

class PremiseRequest(
        @SerializedName("id")
        val id:Int,
        @SerializedName("house_id")
        val house_id:Int?,
        @SerializedName("number")
        val number:String?
)

class Comment(
        @SerializedName("id")
        val id:Int?,
        @SerializedName("created_at")
        val created_at:Long?,
        @SerializedName("initiator")
        val initiator:Initiator?,
        @SerializedName("message")
        val message: String?,
        @SerializedName("filename")
        val filename:String?,
        @SerializedName("filetype")
        val filetype:String?,
        @SerializedName("downloadLink")
        val downloadLink:String?
)

class Initiator(
        @SerializedName("id")
        val id:Int,
        @SerializedName("username")
        val username:String?,
        @SerializedName("name")
        val name:String?,
        @SerializedName("phone")
        val phone: String?,
        @SerializedName("info")
        val info:String?
)

class HouseRequest(
        @SerializedName("id")
        val id:Int,
        @SerializedName("company_id")
        val company_id:Int?,
        @SerializedName("code")
        val code:String?,
        @SerializedName("address")
        val address: String?,
        @SerializedName("fias")
        val fias: String?,
        @SerializedName("cadastral_number")
        val cadastral_number: String?
)

class StateRequest(
        @SerializedName("id")
        val id:Int,
        @SerializedName("name")
        val name:String?,
        @SerializedName("sort")
        val sort:Int?,
        @SerializedName("process_state")
        val process_state: Int?,
        @SerializedName("stateLabel")
        val stateLabel: String?
)

class TypeRequest(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?
)

class TypeHouse(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("sort")
        val sort: Int
)

class Company(
        @SerializedName("id")
        val id: Int,
        @SerializedName("type")
        val type:Int?,
        @SerializedName("typeLabel")
        val typeLabel:String?,
        @SerializedName("state")
        val state: Int?,
        @SerializedName("stateLabel")
        val stateLabel: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("full_name")
        val full_name: String?,
        @SerializedName("jur_address")
        val jur_address: String?,
        @SerializedName("fact_address")
        val fact_address: String?,
        @SerializedName("inn")
        val inn: String?,
        @SerializedName("kpp")
        val kpp: String?,
        @SerializedName("ogrn")
        val ogrn: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("phone")
        val phone: String?,
        @SerializedName("phone_number")
        val phone_number: String?
)