package com.software.ssp.erkc.data.realm.models

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmSchema
import io.realm.FieldAttribute



data class Migrations(val CURRENT_VERSION: Long = 1L) : RealmMigration {


    override fun migrate(realm: DynamicRealm?, old_version: Long, new_version: Long) {

        var version = old_version
        val schema = realm?.getSchema()

        do {
            when (version) {
                0L -> {
                    schema?.get("RealmIpuValue")
                            ?.addField("userRegistered", Boolean::class.java)
                    version++
                }
                else -> version++
            }
        } while (version < new_version)
    }
}