package com.software.ssp.erkc.data.realm.models

import io.realm.RealmObject


open class RealmStreet(
        open var name: String = "",

        open var query: String = "") : RealmObject() {}