package com.software.ssp.erkc.data.realm.models

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmSchema
import io.realm.FieldAttribute
import java.util.*


data class Migrations(val CURRENT_VERSION: Long = 3L) : RealmMigration {


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
                1L -> {
                    schema?.create("RealmIdName")
                            ?.addField("id", String::class.java)
                            ?.addField("name", String::class.java)

                    schema?.create("RealmIpuDictionary")
                            ?.addRealmListField("locations", schema.get("RealmIdName"))
                            ?.addRealmListField("service_names", schema.get("RealmIdName"))
                            ?.addRealmListField("check_intervals", schema.get("RealmIdName"))
                            ?.addRealmListField("types", schema.get("RealmIdName"))
                            ?.addRealmListField("tariff_types", schema.get("RealmIdName"))
                            ?.addRealmListField("statuses", schema.get("RealmIdName"))
                            ?.addRealmListField("close_reasons", schema.get("RealmIdName"))

                    version++
                }
                2L -> {
                    schema?.get("RealmIpuValue")
                            ?.addField("brand", String::class.java)
                            ?.addField("model", String::class.java)
                            ?.addField("check_interval", String::class.java)
                            ?.addField("type", String::class.java)
                            ?.addField("type_tariff", String::class.java)
                            ?.addField("begin_date", String::class.java)
                            ?.addField("install_date", String::class.java)
                            ?.addField("next_check_date", String::class.java)
                            ?.addField("status", String::class.java)
                    version++
                }
                else -> version++
            }
        } while (version < new_version)
    }
}