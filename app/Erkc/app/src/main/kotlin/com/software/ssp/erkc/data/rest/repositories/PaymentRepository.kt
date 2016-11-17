package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.PaymentDataSource
import com.software.ssp.erkc.data.rest.models.PaymentInit
import rx.Observable
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val paymentDataSource: PaymentDataSource) : Repository() {

    fun init(token: String, code: String, method: Int, summ: Float): Observable<PaymentInit> {
        return paymentDataSource.init(token, code, method, summ).compose(this.applySchedulers<PaymentInit>())
    }
}