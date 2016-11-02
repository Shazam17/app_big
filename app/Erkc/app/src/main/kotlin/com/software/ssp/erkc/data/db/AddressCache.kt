package com.software.ssp.erkc.data.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @author Alexander Popov on 01/11/2016.
 */
open class AddressCache(
        @PrimaryKey
        open var id: Long = 0,

        open var name: String = "",

        open var query: String = ""
) : RealmObject() {
}