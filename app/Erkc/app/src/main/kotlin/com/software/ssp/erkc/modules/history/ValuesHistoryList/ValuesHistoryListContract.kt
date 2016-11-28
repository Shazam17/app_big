package com.software.ssp.erkc.modules.history.ValuesHistoryList

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.realm.models.RealmIpuValue


interface IValuesHistoryListView: IListView<RealmIpuValue> {
    fun navigateToIpuValueInfo(ipuValue: RealmIpuValue)
}

interface IValuesHistoryListPresenter: IListPresenter<RealmIpuValue, IValuesHistoryListView> {
    fun onIpuValueClick(ipuValue: RealmIpuValue)
}
