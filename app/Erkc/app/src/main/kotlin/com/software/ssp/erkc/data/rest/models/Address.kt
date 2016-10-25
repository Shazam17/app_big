package com.software.ssp.erkc.data.rest.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Alexander Popov on 25/10/2016.
 */
open class Address(
        @PrimaryKey
        open var id: Long = 0,

        open var name: String = ""
) : RealmObject() {

}

