package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.ReceiptsDataSource
import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.models.Receipt
import rx.Observable
import javax.inject.Inject

class ReceiptsRepository @Inject constructor(private val receiptsDataSource: ReceiptsDataSource) : Repository() {

    fun fetchReceiptInfo(token: String, code: String, street: String = "", house: String = "", apart: String = ""): Observable<Receipt> {
        val params = hashMapOf(
                "token" to token,
                "code" to code)

        if (!street.isNullOrBlank()) {
            params.put("street", street)
            params.put("house", house)
            params.put("apart", apart)
        }

        return receiptsDataSource
                .fetchReceiptInfo(params).compose(this.applySchedulers<Receipt>())
    }

    fun fetchReceipts(token: String): Observable<List<Receipt>> {
        return receiptsDataSource
                .fetchReceipts(token)
                .compose(this.applySchedulers<List<Receipt>>())
    }

    fun deleteReceipt(token: String, receiptId: String): Observable<ApiResponse> {
        val params = hashMapOf(
                "token" to token,
                "id" to receiptId)

        return receiptsDataSource
                .deleteReceipt(params)
                .compose(this.applySchedulers<ApiResponse>())
    }

    fun updateReceipt(token: String,
                      receiptId: String,
                      user_card_id: String,
                      maxsumma: String,
                      mode_id: String
                      ): Observable<ApiResponse> {
        val params = hashMapOf(
                "token" to token,
                "id" to receiptId,
                "user_card_id" to user_card_id,
                "maxsumma" to maxsumma,
                "mode_id" to mode_id
                )

        return receiptsDataSource
                .updateReceipt(params)
                .compose(this.applySchedulers<ApiResponse>())
    }

    fun clearReceiptSettings(token: String, id: String): Observable<ApiResponse> {
        return receiptsDataSource
                .clearReceiptSettings(token, id)
                .compose(this.applySchedulers<ApiResponse>())
    }
}
