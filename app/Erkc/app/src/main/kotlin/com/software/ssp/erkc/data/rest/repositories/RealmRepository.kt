package com.software.ssp.erkc.data.rest.repositories


import com.software.ssp.erkc.data.realm.models.*
import com.software.ssp.erkc.data.rest.models.Card
import com.software.ssp.erkc.data.rest.models.Receipt
import com.software.ssp.erkc.data.rest.models.Streets
import com.software.ssp.erkc.data.rest.models.User
import io.realm.Realm
import rx.Observable
import javax.inject.Inject


class RealmRepository @Inject constructor(private val realm: Realm) : Repository() {

    fun close() {
        realm.close()
    }

    fun fetchStreets(query: String = ""): Observable<List<RealmStreet>> {
        return realm
                .where(RealmStreet::class.java)
                .contains("query", query.toLowerCase())
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .first()
                .flatMap { results ->
                    Observable.just(realm.copyFromRealm(results))
                }
    }

    fun saveStreetList(streets: Streets): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.delete(RealmStreet::class.java)
                        it.copyToRealm(streets.street.map { RealmStreet(it, it.toLowerCase()) })
                    },
                    {
                        sub.onNext(true)
                    },
                    { error ->
                        sub.onError(error)
                    }
            )
        }
    }

    fun streetsLoaded(): Boolean {
        return realm.where(RealmStreet::class.java).count() > 0
    }

    fun updateOfflineSettings(userSettings: OfflineUserSettings): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    { it.copyToRealmOrUpdate(userSettings) },
                    {
                        sub.onNext(true)
                        sub.onCompleted()
                    },
                    { error ->
                        sub.onError(error)
                    }
            )
        }
    }

    fun fetchUser(user: User): Observable<RealmUser> {
        return realm
                .where(RealmUser::class.java)
                .equalTo("login", user.login)
                .findAll()
                .asObservable()
                .filter { it.isLoaded }
                .first()
                .flatMap { results ->
                    val firstResult = results.firstOrNull()
                    if (firstResult == null) {
                        Observable.just(RealmUser(
                                user.login,
                                user.email,
                                user.name,
                                settings = OfflineUserSettings(user.login)))
                    } else {
                        Observable.just(realm.copyFromRealm(firstResult))
                    }
                }
    }

    fun fetchCurrentUser(): Observable<RealmUser> {
        return realm
                .where(RealmUser::class.java)
                .equalTo("isCurrentUser", true)
                .findAll()
                .asObservable()
                .filter { it.isLoaded }
                .first()
                .flatMap { results ->
                    val firstResult = results.firstOrNull()
                    if (firstResult == null) {
                        Observable.just(null)
                    } else {
                        Observable.just(realm.copyFromRealm(firstResult))
                    }
                }
    }

    fun setCurrentUser(user: RealmUser): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    user.isCurrentUser = true
                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    if (currentUser != null) {
                                        currentUser.isCurrentUser = false
                                        it.copyToRealmOrUpdate(currentUser)
                                    }
                                    it.copyToRealmOrUpdate(user)
                                },
                                {
                                    sub.onNext(true)
                                },
                                { error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }

    fun updateUser(user: RealmUser): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    { it.copyToRealmOrUpdate(user) },
                    {
                        sub.onNext(true)
                    },
                    { error ->
                        sub.onError(error)
                    }
            )
        }
    }

    fun saveReceipt(receipt: Receipt): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    val realmReceipt = RealmReceipt(
                            receipt.id!!,
                            receipt.street,
                            receipt.house,
                            receipt.apart,
                            receipt.autoPayMode,
                            receipt.name,
                            receipt.maxSumm,
                            receipt.lastPayment,
                            receipt.address,
                            receipt.serviceCode,
                            receipt.amount,
                            receipt.barcode,
                            receipt.lastValueTransfer,
                            receipt.supplierName,
                            receipt.persent,
                            if (receipt.linkedCardId == null) null else realm.where(RealmCard::class.java).equalTo("id", receipt.linkedCardId).findFirst())

                    if (currentUser.receipts.find { it.id == receipt.id } == null) {
                        currentUser.receipts.add(realmReceipt)
                    }

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(realmReceipt)
                                    it.copyToRealmOrUpdate(currentUser)
                                },
                                {
                                    sub.onNext(true)
                                },
                                { error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }

    fun saveReceiptsList(receipts: List<Receipt>): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    val cachedReceipts = arrayListOf<RealmReceipt>()
                    receipts.mapTo(cachedReceipts) {
                        RealmReceipt(
                                it.id!!,
                                it.street,
                                it.house,
                                it.apart,
                                it.autoPayMode,
                                it.name,
                                it.maxSumm,
                                it.lastPayment,
                                it.address,
                                it.serviceCode,
                                it.amount,
                                it.barcode,
                                it.lastValueTransfer,
                                it.supplierName,
                                it.persent,
                                if (it.linkedCardId == null) null else realm.where(RealmCard::class.java).equalTo("id", it.linkedCardId).findFirst())
                    }

                    currentUser.receipts.clear()
                    currentUser.receipts.addAll(cachedReceipts)

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(cachedReceipts)
                                    it.copyToRealmOrUpdate(currentUser)
                                },
                                {
                                    sub.onNext(true)
                                },
                                { error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }

    fun fetchReceiptsList(): Observable<List<RealmReceipt>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser?.receipts?.sortedBy { it.address })
                }
    }

    fun removeReceipt(receipt: RealmReceipt): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.where(RealmReceipt::class.java)
                                .equalTo("id", receipt.id)
                                .findFirst()
                                .deleteFromRealm()
                    },
                    {
                        sub.onNext(true)
                    },
                    { error ->
                        sub.onError(error)
                    }
            )
        }
    }

    fun saveCardsList(cards: List<Card>): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    val cachedCards = arrayListOf<RealmCard>()
                    cards.mapTo(cachedCards) {
                        RealmCard(
                                it.id,
                                it.name,
                                it.statusId,
                                it.remoteCardId,
                                it.maskCardNo,
                                it.statusStr)
                    }

                    currentUser.cards.clear()
                    currentUser.cards.addAll(cachedCards)

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(cachedCards)
                                    it.copyToRealmOrUpdate(currentUser)
                                },
                                {
                                    sub.onNext(true)
                                },
                                { error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }

    fun removeCard(card: RealmCard): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.where(RealmCard::class.java)
                                .equalTo("id", card.id)
                                .findFirst()
                                .deleteFromRealm()
                    },
                    {
                        sub.onNext(true)
                    },
                    { error ->
                        sub.onError(error)
                    }
            )
        }
    }

    fun fetchCardsList(): Observable<List<RealmCard>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser?.cards)
                }
    }

    fun saveCard(card: Card): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    val realmCard = RealmCard(
                            card.id,
                            card.name,
                            card.statusId,
                            card.remoteCardId,
                            card.maskCardNo,
                            card.statusStr)

                    if (currentUser.cards.find { it.id == card.id } == null) {
                        currentUser.cards.add(realmCard)
                    }

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(realmCard)
                                    it.copyToRealmOrUpdate(currentUser)
                                },
                                {
                                    sub.onNext(true)
                                },
                                { error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }
}
