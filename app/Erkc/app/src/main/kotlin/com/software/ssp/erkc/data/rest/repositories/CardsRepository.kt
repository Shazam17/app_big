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

    fun fetchCards(token: String): Observable<List<Card>> {
        return cardsDataSource.fetchCards(token).compose(this.applySchedulers<List<Card>>())
    }

    fun fetchCard(token: String, id: String): Observable<Card> {
        return cardsDataSource.fetchCard(token, id).compose(this.applySchedulers<Card>())
    }

    fun addCard(token: String, name: String): Observable<Card> {
        return cardsDataSource.add(token, name).compose(this.applySchedulers<Card>())
    }

    fun registrateCard(token: String, id: String): Observable<CardRegistration> {
        return cardsDataSource.registration(token, id).compose(this.applySchedulers<CardRegistration>())
    }

    fun activateCard(token: String, id: String): Observable<CardActivation> {
        return cardsDataSource.activation(token, id).compose(this.applySchedulers<CardActivation>())
    }

    fun deleteCard(token: String, id: String): Observable<ResponseBody> {
        return cardsDataSource.deleteCard(token, id).compose(this.applySchedulers<ResponseBody>())
    }

    fun updateCard(token: String, id: String, name: String) : Observable<ResponseBody> {
        return cardsDataSource.updateCard(mapOf("token" to token, "id" to id, "name" to name)).compose(this.applySchedulers<ResponseBody>())
    }
}