package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmRequest(
        @PrimaryKey
        open var id:Int=0,
        open var state:String?="",
        open var title:String?="",
        open var type:String?=""
):RealmObject()