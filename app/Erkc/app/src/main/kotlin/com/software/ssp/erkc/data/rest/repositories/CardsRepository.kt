package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.CardsDataSource
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.CardActivation
import com.software.ssp.erkc.data.rest.models.CardRegistration
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 10/11/2016.
 */

class CardsRepository @Inject constructor(private val cardsDataSource: CardsDataSource) : Repository() {

    fun fetchCards(): Observable<List<Card>> {
        return cardsDataSource.fetchCards().compose(this.applySchedulers<List<Card>>())
    }

    fun fetchCard(id: String): Observable<Card> {
        return cardsDataSource.fetchCard(id).compose(this.applySchedulers<Card>())
    }

    fun addCard(name: String): Observable<Card> {
        return cardsDataSource.add(name).compose(this.applySchedulers<Card>())
    }

    fun registerCard(id: String): Observable<CardRegistration> {
        return cardsDataSource.registration(id).compose(this.applySchedulers<CardRegistration>())
    }

    fun activateCard(id: String): Observable<CardActivation> {
        return cardsDataSource.activation(id).compose(this.applySchedulers<CardActivation>())
    }

    fun deleteCard(id: String): Observable<ResponseBody> {
        return cardsDataSource.deleteCard(id).compose(this.applySchedulers<ResponseBody>())
    }

    fun updateCard(id: String, name: String): Observable<ResponseBody> {
        return cardsDataSource.updateCard(
                mapOf(
                        "id" to id,
                        "name" to name
                )
        ).compose(this.applySchedulers<ResponseBody>())
    }
}