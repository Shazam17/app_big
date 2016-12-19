package com.software.ssp.erkc.data.rest.repositories


import com.software.ssp.erkc.data.realm.models.*
import com.software.ssp.erkc.data.rest.models.*
import io.realm.Realm
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

    fun streetsLoaded(): Boolean {
        return realm.where(RealmStreet::class.java).count() > 0
    }

    fun updateOfflineSettings(userSettings: RealmSettings): Observable<Boolean> {
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
                                settings = RealmSettings(
                                        login = user.login,
                                        pushEnabled = true,
                                        paymentNotificationEnabled = true,
                                        operationStatusNotificationEnabled = true,
                                        ipuNotificationEnabled = true,
                                        newsNotificationEnabled = true)
                        )
                        )
                    } else {
                        Observable.just(realm.copyFromRealm(firstResult))
                    }
                }
    }

    fun fetchUser(userLogin: String): Observable<RealmUser> {
        return realm
                .where(RealmUser::class.java)
                .equalTo("login", userLogin)
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

    fun fetchOfflinePayments(): Observable<List<RealmOfflinePayment>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    realm
                            .where(RealmOfflinePayment::class.java)
                            .equalTo("login", currentUser.login)
                            .findAll()
                            .asObservable()
                            .filter { it.isLoaded }
                            .first()
                }
                .flatMap {
                    Observable.just(realm.copyFromRealm(it))
                }
    }

    fun fetchOfflinePaymentsByReceiptId(login: String, receiptId: String): Observable<RealmOfflinePayment> {
        return realm.where(RealmOfflinePayment::class.java)
                .equalTo("login", login)
                .equalTo("receipt.id", receiptId)
                .findAll()
                .asObservable()
                .first()
                .flatMap {
                    result ->
                    val firstResult = result.firstOrNull()
                    if (firstResult == null) {
                        Observable.just(null)
                    } else {
                        Observable.just(realm.copyFromRealm(firstResult))
                    }
                }
    }

    fun fetchOfflineIpuByReceiptId(receiptId: String): Observable<List<RealmOfflineIpu>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    realm.where(RealmOfflineIpu::class.java)
                            .equalTo("login", currentUser.login)
                            .equalTo("receipt.id", receiptId)
                            .findAll()
                            .asObservable()
                            .flatMap {
                                Observable.just(realm.copyFromRealm(it))
                            }
                }
    }

    fun fetchOfflineIpu(): Observable<List<RealmOfflineIpu>> {
        return fetchCurrentUser().concatMap {
            currentUser ->
            realm
                    .where(RealmOfflineIpu::class.java)
                    .equalTo("login", currentUser.login)
                    .findAll()
                    .asObservable()
                    .flatMap {
                        Observable.just(realm.copyFromRealm(it))
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

    fun updateSettings(settings: Settings): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    currentUser.settings?.apply {
                        operationStatusNotificationEnabled = if (settings.operationsNotificationStatus == 1) true else false
                        newsNotificationEnabled = if (settings.newsNotificationStatus == 1) true else false
                        paymentNotificationEnabled = if (settings.paymentRemindStatus == 1) true else false
                        ipuNotificationEnabled = if (settings.ipuRemindStatus == 1) true else false

                        pushEnabled = operationStatusNotificationEnabled
                                || newsNotificationEnabled
                                || paymentNotificationEnabled
                                || ipuNotificationEnabled
                    }

                    updateUser(currentUser)
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
                        linkedCard = if (receipt.linkedCardId == null) null else realm.copyFromRealm(realm.where(RealmCard::class.java).equalTo("id", receipt.linkedCardId).findFirst())
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
                        sum = paymentInfo.sum
                        supplierName = paymentInfo.supplierName
                        serviceName = paymentInfo.serviceName
                        amount = paymentInfo.amount
                        text = paymentInfo.text
                        modeId = paymentInfo.modeId
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
                                it.checkFile ?: "",
                                it.status,
                                it.errorDesc,
                                it.operationId,
                                it.modeId,
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

    fun saveOfflinePayment(realmReceipt: RealmReceipt, paymentSum: Double, email: String, card: RealmCard?): Observable<Boolean> {
        return fetchCurrentUser().concatMap {
            currentUser ->
            Observable.create<Boolean> { sub ->
                realm.executeTransactionAsync(
                        {
                            val realmOfflinePayment = it.where(RealmOfflinePayment::class.java)
                                    .equalTo("receipt.id", realmReceipt.id)
                                    .equalTo("login", currentUser.login)
                                    .findFirst()
                            realmOfflinePayment?.deleteFromRealm()
                            it.copyToRealm(RealmOfflinePayment(currentUser.login,
                                    it.where(RealmReceipt::class.java).equalTo("barcode",
                                            realmReceipt.barcode).findFirst(),
                                    paymentSum,
                                    email,
                                    it.where(RealmCard::class.java).equalTo("id", card?.id).findFirst()))
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

    fun saveOfflineIpu(code: String, params: HashMap<String, String>): Observable<Boolean> {
        return fetchCurrentUser().concatMap {
            currentUser ->
            Observable.create<Boolean> { sub ->
                realm.executeTransactionAsync(
                        {
                            it.where(RealmOfflineIpu::class.java).equalTo("receipt.barcode", code).findAll().deleteAllFromRealm()
                            for ((key, value) in params) {
                                it.copyToRealm(RealmOfflineIpu(currentUser.login,
                                        it.where(RealmReceipt::class.java).equalTo("barcode", code).findFirst(),
                                        key,
                                        value))
                            }
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

    fun deleteOfflineIpu(receiptId: String): Observable<Boolean> {
        return fetchCurrentUser().concatMap {
            currentUser ->
            Observable.create<Boolean> { sub ->
                realm.executeTransactionAsync(
                        {
                            it.where(RealmOfflineIpu::class.java)
                                    .equalTo("receipt.id", receiptId)
                                    .equalTo("login", currentUser.login).findAll().deleteAllFromRealm()
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

    fun deleteOfflinePayment(payment: RealmOfflinePayment): Observable<Boolean> {
        return fetchCurrentUser().concatMap {
            currentUser ->
            Observable.create<Boolean> { sub ->
                realm.executeTransactionAsync(
                        {
                            it.where(RealmOfflinePayment::class.java)
                                    .equalTo("receipt.id", payment.receipt.id)
                                    .equalTo("login", currentUser.login).findAll().deleteAllFromRealm()
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

    fun fetchIpuList(): Observable<List<RealmIpu>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser.ipus)
                }
    }

    fun fetchIpuByReceiptId(receiptId: String): Observable<RealmIpu> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    var ipu = currentUser.ipus.find { it.receiptId == receiptId }

                    if (ipu == null) {
                        ipu = RealmIpu(
                                receiptId = receiptId,
                                receipt = realm.copyFromRealm(realm.where(RealmReceipt::class.java).equalTo("id", receiptId).findFirst())
                        )
                    }

                    Observable.just(ipu)
                }
    }

    fun saveIpu(ipu: RealmIpu): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->

                    val index = currentUser.ipus.indexOfFirst { it.receiptId == ipu.receiptId }
                    if(index < 0) {
                        currentUser.ipus.add(ipu)
                    } else {
                        currentUser.ipus[index] = ipu
                    }

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

    fun saveIpusWithReceipt(ipus: List<Ipu>, receiptId: String): Observable<Boolean> {
        return fetchIpuByReceiptId(receiptId)
                .concatMap {
                    realmIpu ->

                    realmIpu.apply {
                        ipuValues.clear()
                        ipuValues.addAll(ipus.map({
                            RealmIpuValue(
                                    id = it.id,
                                    serviceName = it.serviceName,
                                    number = it.number,
                                    installPlace = it.installPlace,
                                    date = it.date,
                                    value = it.value,
                                    isSent = true
                            )
                        }))
                    }

                    saveIpu(realmIpu)
                }
    }
}
