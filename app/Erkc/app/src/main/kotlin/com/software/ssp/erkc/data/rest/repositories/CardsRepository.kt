package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.CardsDataSource
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.CardRegistration
import com.software.ssp.erkc.data.rest.models.DataResponse
import rx.Observable
import javax.inject.Inject

class CardsRepository @Inject constructor(private val cardsDataSource: CardsDataSource) : Repository() {

    fun fetchCards(token: String): Observable<DataResponse<List<Card>>> {
        return cardsDataSource.fetchCards(token).compose(this.applySchedulers<DataResponse<List<Card>>>())
    }

    fun addCard(token: String, name: String): Observable<DataResponse<Card>> {
        return cardsDataSource.add(token, name).compose(this.applySchedulers<DataResponse<Card>>())
    }

    fun registrateCard(token: String, id: String): Observable<DataResponse<CardRegistration>> {
        return cardsDataSource.registration(token, id).compose(this.applySchedulers<DataResponse<CardRegistration>>())
    }
}
