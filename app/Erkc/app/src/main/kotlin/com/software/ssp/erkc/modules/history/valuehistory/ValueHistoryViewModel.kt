package com.software.ssp.erkc.modules.history.valuehistory

import com.software.ssp.erkc.data.realm.models.RealmIpuValue


class ValueHistoryViewModel(
        val values: List<RealmIpuValue>,
        val total: Long,
        val average: Long
)