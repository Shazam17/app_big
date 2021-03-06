package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.PaymentDataSource
import com.software.ssp.erkc.data.rest.models.PaymentCheck
import com.software.ssp.erkc.data.rest.models.PaymentInfo
import com.software.ssp.erkc.data.rest.models.PaymentInit
import rx.Observable
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val paymentDataSource: PaymentDataSource) : Repository() {

    fun init(code: String, method: Int, sum: String, email: String, cardId: String?): Observable<PaymentInit> {
        val params = mutableMapOf(
                "code" to code,
                "summ" to sum,
                "method_id" to method.toString()
        )
        if (cardId != null) {
            params.put("user_card_id", cardId)
        }

        params.put("email", email)

        return paymentDataSource.init(params).compose(this.applySchedulers<PaymentInit>())
    }

    fun fetchPayments(): Observable<List<PaymentInfo>> {
        return paymentDataSource
                .getUserPayments()
                .compose(this.applySchedulers<List<PaymentInfo>>())
    }

    fun fetchCheck(id: String): Observable<PaymentCheck> {
        return paymentDataSource
                .getCheck(id)
                .compose(this.applySchedulers<PaymentCheck>())
    }

    fun fetchPaymentInfo(id: String): Observable<PaymentInfo> {
        return paymentDataSource
                .getPaymentsById(id)
                .compose(this.applySchedulers<PaymentInfo>())
    }
}
