package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.PaymentDataSource
import com.software.ssp.erkc.data.rest.models.PaymentInit
import rx.Observable
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val paymentDataSource: PaymentDataSource) : Repository() {

    fun init(token: String, code: String, method: Int, summ: Float, email: String, cardId: String?): Observable<PaymentInit> {
        val params = hashMapOf(
                "token" to token,
                "code" to code,
                "method_id" to method.toString(),
                "summ" to summ.toString(),
                "email" to email
        )
        if (cardId != null) {
            params.put("user_card_id", cardId)
        }
        return paymentDataSource.init(params).compose(this.applySchedulers<PaymentInit>())
    }
}