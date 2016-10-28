package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.CardsDataSource
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.DataResponse
import rx.Observable
import javax.inject.Inject

class CardsRepository @Inject constructor(private val cardsDataSource: CardsDataSource) : Repository() {

    fun fetchCards(token: String): Observable<DataResponse<List<Card>>> {
        return cardsDataSource.fetchCards(token).compose(this.applySchedulers<DataResponse<List<Card>>>())
    }
}
