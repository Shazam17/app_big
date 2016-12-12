package com.software.ssp.erkc.data.rest.repositories


import com.software.ssp.erkc.data.realm.models.*
import com.software.ssp.erkc.data.rest.models.*
import io.realm.Realm
import io.realm.RealmList
import rx.Observable
import java.util.*
import javax.inject.Inject
import kotlin.comparisons.compareBy


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

    fun saveIpusByBarсode(ipus: List<Ipu>, receipt: RealmReceipt): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    val realmIpu = RealmIpu(ipus.mapTo(RealmList<RealmIpuValue>()) {
                        RealmIpuValue(it.id,
                                it.serviceName,
                                it.number,
                                it.installPlace,
                                it.date,
                                it.value)
                    }, receipt)
                    val list = RealmList<RealmIpu>()
                    list += currentUser.ipus.filter { it.receipt?.id != receipt.id }
                    list.add(realmIpu)
                    currentUser.ipus = list
                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
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
                    var realmReceipt = currentUser.receipts.find { it.id == receipt.id }

                    if (realmReceipt == null) {
                        realmReceipt = RealmReceipt(receipt.id!!)
                        currentUser.receipts.add(realmReceipt)
                    }

                    realmReceipt.apply {
                        street = receipt.street
                        house = receipt.house
                        apart = receipt.apart
                        autoPayMode = receipt.autoPayMode
                        name = receipt.name
                        maxSum = receipt.maxSumm
                        lastPaymentDate = receipt.lastPaymentDate
                        address = receipt.address
                        serviceCode = receipt.serviceCode
                        amount = receipt.amount
                        barcode = receipt.barcode
                        lastIpuTransferDate = receipt.lastIpuTransferDate
                        supplierName = receipt.supplierName
                        percent = receipt.percent
                        linkedCard = realm.copyFromRealm(realm.where(RealmCard::class.java).equalTo("id", receipt.linkedCardId).findFirst())
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
                        var linkedCard: RealmCard? = null
                        if (it.linkedCardId != null) {
                            linkedCard = realm.where(RealmCard::class.java).equalTo("id", it.linkedCardId).findFirst()
                        }
                        RealmReceipt(
                                it.id!!,
                                it.street,
                                it.house,
                                it.apart,
                                it.autoPayMode,
                                it.name,
                                it.maxSumm,
                                it.lastPaymentDate,
                                it.address,
                                it.serviceCode,
                                it.amount,
                                it.barcode,
                                it.lastIpuTransferDate,
                                it.supplierName,
                                it.percent,
                                if (linkedCard == null) null else realm.copyFromRealm(linkedCard)
                        )
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

    fun fetchReceiptsById(receiptId: String): Observable<RealmReceipt> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser?.receipts?.first { it.id == receiptId })
                }
    }

    fun fetchIpuValuesList(receipt: RealmReceipt): Observable<List<RealmIpuValue>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser?.ipus
                            ?.first { it.receipt?.id == receipt.id }?.ipuValues
                            ?.sortedBy { it.number })
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

                    var realmCard = currentUser.cards.find { it.id == card.id }

                    if (realmCard == null) {
                        realmCard = RealmCard(card.id)
                        currentUser.cards.add(realmCard)
                    }

                    realmCard.apply {
                        name = card.name
                        statusId = card.statusId
                        remoteCardId = card.remoteCardId
                        maskedCardNumber = card.maskCardNo
                        statusStr = card.statusStr
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

    fun savePayment(payment: Payment): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->

                    var realmPayment = currentUser.payments.find { it.id == payment.id }

                    if (realmPayment == null) {
                        realmPayment = RealmPayment(payment.id)
                        currentUser.payments.add(realmPayment)
                    }

                    realmPayment.apply {
                        date = payment.date
                        amount = payment.amount
                        checkFile = payment.checkFile
                        status = payment.status
                        errorDesc = payment.errorDesc
                        operationId = payment.operationId
                        methodId = payment.methodId
                        receipt = realm.copyFromRealm(realm.where(RealmReceipt::class.java).equalTo("id", payment.receiptId).findFirst())
                    }

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(realmPayment)
                                    it.copyToRealmOrUpdate(currentUser)
                                },
                                {
                                    sub.onNext(true)
                                },
                                {
                                    error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }

    fun savePaymentInfo(paymentInfo: PaymentInfo): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->

                    var realmPayment = currentUser.paymentsInfo.find { it.id == paymentInfo.id }

                    if (realmPayment == null) {
                        realmPayment = RealmPaymentInfo(paymentInfo.id)
                        currentUser.paymentsInfo.add(realmPayment)
                    }

                    realmPayment.apply {
                        date = paymentInfo.date
                        house = paymentInfo.house
                        status = paymentInfo.status
                        street = paymentInfo.street
                        barcode = paymentInfo.barcode
                        operationId = paymentInfo.operationId
                        summ = paymentInfo.summ
                        supplierName = paymentInfo.supplierName
                        serviceName = paymentInfo.serviceName
                        amount = paymentInfo.amount
                        text = paymentInfo.text
                        address = paymentInfo.address
                        receipt = realm.copyFromRealm(realm.where(RealmReceipt::class.java).equalTo("id", paymentInfo.receiptId).findFirst())
                        apart = paymentInfo.apart

                    }

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(realmPayment)
                                    it.copyToRealmOrUpdate(currentUser)
                                },
                                {
                                    sub.onNext(true)
                                },
                                {
                                    error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }

    fun savePaymentsList(payments: List<Payment>): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    val cachedPayments = arrayListOf<RealmPayment>()
                    payments.mapTo(cachedPayments) {
                        RealmPayment(
                                it.id,
                                it.date,
                                it.amount,
                                it.checkFile,
                                it.status,
                                it.errorDesc,
                                it.operationId,
                                it.methodId,
                                realm.copyFromRealm(realm.where(RealmReceipt::class.java).equalTo("id", it.receiptId).findFirst())
                        )
                    }

                    currentUser.payments.clear()
                    currentUser.payments.addAll(cachedPayments)

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(cachedPayments)
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

    fun fetchPayments(): Observable<List<RealmPayment>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser.payments.sortedWith(compareBy({ it.receipt?.address }, { it.date })))
                }
    }

    fun fetchPaymentInfoById(id: String): Observable<RealmPaymentInfo> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser.paymentsInfo.filter { it.id == id }.first())
                }
    }

    fun fetchPaymentById(id: String): Observable<RealmPayment> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser.payments.filter { it.id == id }.first())
                }
    }
}
