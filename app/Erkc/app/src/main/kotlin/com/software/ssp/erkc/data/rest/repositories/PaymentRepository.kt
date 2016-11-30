package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.PaymentDataSource
import com.software.ssp.erkc.data.rest.models.Payment
import com.software.ssp.erkc.data.rest.models.PaymentCheck
import com.software.ssp.erkc.data.rest.models.PaymentInit
import rx.Observable
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val paymentDataSource: PaymentDataSource) : Repository() {

    fun init(token: String, code: String, method: Int, summ: String, email: String, cardId: String?): Observable<PaymentInit> {
        val params = hashMapOf(
                "token" to token,
                "code" to code,
                "method_id" to method.toString(),
                "summ" to summ,
                "email" to email
        )
        if (cardId != null) {
            params.put("user_card_id", cardId)
        }
        return paymentDataSource.init(params).compose(this.applySchedulers<PaymentInit>())
    }

    fun fetchPayments(token: String, receiptCode: String): Observable<List<Payment>> {
        return paymentDataSource
                .getByReceipt(token, receiptCode)
                .compose(this.applySchedulers<List<Payment>>())
    }

    fun fetchCheck(token: String, id: String): Observable<PaymentCheck> {
        return paymentDataSource
                .getCheck(token, id)
                .compose(this.applySchedulers<PaymentCheck>())
    }
}
