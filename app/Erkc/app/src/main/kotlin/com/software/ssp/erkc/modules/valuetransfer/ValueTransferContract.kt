package com.software.ssp.erkc.modules.valuetransfer

import com.software.ssp.erkc.common.mvp.IListPresenter
import com.software.ssp.erkc.common.mvp.IListView
import com.software.ssp.erkc.data.rest.models.Receipt


interface IValueTransferView : IListView<Receipt> {
}

interface IValueTransferPresenter : IListPresenter<Receipt, IValueTransferView> {
}

