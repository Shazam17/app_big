package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.ReceiptsDataSource
import com.software.ssp.erkc.data.rest.models.DataResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import rx.Observable
import javax.inject.Inject

class ReceiptsRepository @Inject constructor(private val receiptsDataSource: ReceiptsDataSource) : Repository() {

    fun fetchReceiptInfo(token: String, code: String, street: String, house: String, apart: String): Observable<DataResponse<Receipt>> {
        val params = mapOf("token" to token, "code" to code)
        if (!street.isBlank())
            params.plus("street" to street)
        if (!house.isBlank())
            params.plus("house" to house)
        if (!apart.isBlank())
            params.plus("apart" to apart)
        return receiptsDataSource
                .fetchReceiptInfo(params).compose(this.applySchedulers<DataResponse<Receipt>>())
    }

}