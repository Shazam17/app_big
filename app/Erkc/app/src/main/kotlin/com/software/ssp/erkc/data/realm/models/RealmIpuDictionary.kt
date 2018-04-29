package com.software.ssp.erkc.data.realm.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class RealmIpuDictionary(
        open var locations: RealmList<RealmIdName> = RealmList(),
        open var service_names: RealmList<RealmIdName> = RealmList(),
        open var check_intervals: RealmList<RealmIdName> = RealmList(),
        open var types: RealmList<RealmIdName> = RealmList(),
        open var tariff_types: RealmList<RealmIdName> = RealmList(),
        open var statuses: RealmList<RealmIdName> = RealmList(),
        open var close_reasons: RealmList<RealmIdName> = RealmList()
) : RealmObject()


open class RealmIdName(
        open var id: String = "",
        open var name: String = ""
) : RealmObject()