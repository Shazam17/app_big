package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.ReceiptsDataSource
import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.extensions.toStringWithDot
import rx.Observable
import javax.inject.Inject

class ReceiptsRepository @Inject constructor(private val receiptsDataSource: ReceiptsDataSource) : Repository() {

    fun fetchReceiptInfo(code: String, street: String = "", house: String = "", apart: String = ""): Observable<Receipt> {
        val params = mutableMapOf("code" to code)

        if (!street.isNullOrBlank()) {
            params.put("street", street)
            params.put("house", house)
            params.put("apart", apart)
        }

        return receiptsDataSource
                .fetchReceiptInfo(params).compose(this.applySchedulers<Receipt>())
    }

    fun fetchReceipts(): Observable<List<Receipt>> {
        return receiptsDataSource
                .fetchReceipts()
                .compose(this.applySchedulers<List<Receipt>>())
    }

    fun deleteReceipt(receiptId: String): Observable<ApiResponse> {
        return receiptsDataSource
                .deleteReceipt(receiptId)
                .compose(this.applySchedulers<ApiResponse>())
    }

    fun updateReceipt(receiptId: String,
                      userCardId: String,
                      maxSum: Double,
                      modeId: String): Observable<ApiResponse> {
        val params = mapOf(
                "id" to receiptId,
                "user_card_id" to userCardId,
                "maxsumma" to maxSum.toStringWithDot(),
                "mode_id" to modeId
        )

        return receiptsDataSource
                .updateReceipt(params)
                .compose(this.applySchedulers<ApiResponse>())
    }

    fun clearReceiptSettings(id: String): Observable<ApiResponse> {
        return receiptsDataSource
                .clearReceiptSettings(id)
                .compose(this.applySchedulers<ApiResponse>())
    }
}
