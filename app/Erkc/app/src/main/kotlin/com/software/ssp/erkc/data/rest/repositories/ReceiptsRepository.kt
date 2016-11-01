package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.ReceiptsDataSource
import com.software.ssp.erkc.data.rest.models.Receipt
import rx.Observable
import javax.inject.Inject

class ReceiptsRepository @Inject constructor(private val receiptsDataSource: ReceiptsDataSource) : Repository() {

    fun fetchReceiptInfo(token: String, code: String, street: String, house: String, apart: String): Observable<Receipt> {
        val params = hashMapOf(
                "token" to token,
                "code" to code,
                "street" to street,
                "house" to house,
                "apart" to apart)

        return receiptsDataSource
                .fetchReceiptInfo(params)
                .compose(this.applySchedulers<Receipt>())
    }

    fun fetchReceipts(token: String): Observable<List<Receipt>>{
        return receiptsDataSource
                .fetchReceipts(token)
                .compose(this.applySchedulers<List<Receipt>>())
    }
}
