package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.ReceiptsDataSource
import com.software.ssp.erkc.data.rest.models.DataResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import rx.Observable
import javax.inject.Inject

class ReceiptsRepository @Inject constructor(private val receiptsDataSource: ReceiptsDataSource) : Repository() {

    fun fetchReceiptInfo(token: String, code: String, street: String, house: String, apart: String): Observable<DataResponse<Receipt>> {
        return receiptsDataSource
                .fetchReceiptInfo(token, code, street, house, apart)
                .compose(this.applySchedulers<DataResponse<Receipt>>())
    }

}