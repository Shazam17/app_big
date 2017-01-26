package com.software.ssp.erkc.data.rest.repositories


import com.software.ssp.erkc.data.realm.models.*
import com.software.ssp.erkc.data.rest.models.*
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
                    realm.where(RealmOfflinePayment::class.java)
                            .equalTo("login", currentUser.login)
                            .findAll()
                            .asObservable()
                            .filter { it.isLoaded }
                            .first()
                }
                .flatMap {
                    Observable.just(realm.copyFromRealm(it ?: emptyList()))
                }
    }

    fun fetchOfflinePaymentByReceiptId(login: String, receiptId: String): Observable<RealmOfflinePayment> {
        return realm.where(RealmOfflinePayment::class.java)
                .equalTo("login", login)
                .equalTo("receipt.id", receiptId)
                .findAll()
                .asObservable()
                .filter { it.isLoaded }
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

    fun fetchOfflineIpuByReceiptId(receiptId: String): Observable<RealmOfflineIpu> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    realm.where(RealmOfflineIpu::class.java)
                            .equalTo("login", currentUser.login)
                            .equalTo("receipt.id", receiptId)
                            .findAll()
                            .asObservable()
                            .filter { it.isLoaded }
                            .first()
                }
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

    fun fetchOfflineIpus(): Observable<List<RealmOfflineIpu>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    realm.where(RealmOfflineIpu::class.java)
                            .equalTo("login", currentUser.login)
                            .findAll()
                            .asObservable()
                            .filter { it.isLoaded }
                            .first()
                }
                .flatMap {
                    Observable.just(realm.copyFromRealm(it ?: emptyList()))
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

    fun updateReceipt(receipt: RealmReceipt): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.copyToRealmOrUpdate(receipt)
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
                                    it.copyToRealmOrUpdate(currentUser)
                                    it.copyToRealmOrUpdate(cachedReceipts)
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
                    currentUser?.receipts?.toTypedArray()?.sortBy { it.address }
                    Observable.just(currentUser?.receipts?.reversed())
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
                        apart = paymentInfo.apart
                        checkFile = paymentInfo.checkFile
                        errorDesc = paymentInfo.errorDesc
                        receipt = realm.copyFromRealm(realm.where(RealmReceipt::class.java).equalTo("id", paymentInfo.receiptId).findFirst())
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

    fun savePaymentsList(payments: List<PaymentInfo>): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    val cachedPayments = arrayListOf<RealmPaymentInfo>()
                    payments.mapTo(cachedPayments) {
                        RealmPaymentInfo(
                                it.id,
                                it.date,
                                it.house,
                                it.status,
                                it.street,
                                it.barcode,
                                it.operationId,
                                it.modeId,
                                it.sum,
                                it.supplierName,
                                it.serviceName,
                                it.amount,
                                it.text,
                                it.address,
                                it.apart,
                                it.checkFile ?: "",
                                it.errorDesc,
                                realm.copyFromRealm(realm.where(RealmReceipt::class.java).equalTo("id", it.receiptId).findFirst())
                        )
                    }

                    currentUser.paymentsInfo.clear()
                    currentUser.paymentsInfo.addAll(cachedPayments)

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

    fun fetchPayments(): Observable<List<RealmPaymentInfo>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser.paymentsInfo)
                }
    }

    fun fetchPaymentInfoById(id: String): Observable<RealmPaymentInfo> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser.paymentsInfo.filter { it.id == id }.firstOrNull())
                }
    }

    fun saveOfflinePayment(realmOfflinePayment: RealmOfflinePayment): Observable<Boolean> {
        return deleteOfflinePayment(realmOfflinePayment)
                .concatMap {
                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    realmOfflinePayment.receipt = it.where(RealmReceipt::class.java)
                                            .equalTo("id", realmOfflinePayment.receipt.id)
                                            .findFirst()

                                    if (realmOfflinePayment.card != null) {
                                        realmOfflinePayment.card = it.where(RealmCard::class.java)
                                                .equalTo("id", realmOfflinePayment.card!!.id)
                                                .findFirst()
                                    }

                                    it.copyToRealm(realmOfflinePayment)
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

    fun deleteOfflinePayment(payment: RealmOfflinePayment): Observable<Boolean> {
        return fetchCurrentUser().concatMap {
            currentUser ->
            Observable.create<Boolean> { sub ->
                realm.executeTransactionAsync(
                        {
                            it.where(RealmOfflinePayment::class.java)
                                    .equalTo("login", payment.login)
                                    .equalTo("receipt.id", payment.receipt.id)
                                    .findAll()
                                    .deleteAllFromRealm()
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

    fun saveOfflineIpu(realmOfflineIpu: RealmOfflineIpu): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->

                    realmOfflineIpu.login = currentUser.login

                    deleteOfflineIpu(realmOfflineIpu)
                }
                .concatMap {
                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    realmOfflineIpu.receipt = it.where(RealmReceipt::class.java)
                                            .equalTo("id", realmOfflineIpu.receipt.id)
                                            .findFirst()

                                    it.copyToRealm(realmOfflineIpu)
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

    fun deleteOfflineIpu(realmOfflineIpu: RealmOfflineIpu): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.where(RealmOfflineIpu::class.java)
                                .equalTo("receipt.id", realmOfflineIpu.receipt.id)
                                .equalTo("login", realmOfflineIpu.login)
                                .findAll()
                                .deleteAllFromRealm()
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
                    if (index < 0) {
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
                                    sub.onCompleted()
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


    fun saveNotification(notification: Notification): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    var realmNotification = currentUser.notifications.find { it.id == notification.id }

                    if (realmNotification == null) {
                        realmNotification = RealmNotification(notification.id)
                        currentUser.notifications.add(realmNotification)
                    }

                    realmNotification.apply {
                        title = notification.title
                        message = notification.message
                        isRead = notification.isRead == 1
                        deliveredDate = notification.date
                        readDate = notification.readDate
                    }

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(realmNotification)
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

    fun updateNotification(notification: RealmNotification): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.copyToRealmOrUpdate(notification)
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

    fun saveNotificationsList(notifications: List<Notification>): Observable<Boolean> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    val cachedNotifications = arrayListOf<RealmNotification>()

                    notifications.mapTo(cachedNotifications) {
                        RealmNotification(
                                it.id,
                                it.isRead == 1,
                                it.message,
                                it.title,
                                it.date,
                                it.readDate
                        )
                    }

                    currentUser.notifications.clear()
                    currentUser.notifications.addAll(cachedNotifications)

                    Observable.create<Boolean> { sub ->
                        realm.executeTransactionAsync(
                                {
                                    it.copyToRealmOrUpdate(cachedNotifications)
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

    fun fetchNotificationsList(): Observable<List<RealmNotification>> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser?.notifications?.sortedByDescending { it.deliveredDate })
                }
    }

    fun fetchNotificationById(notificationId: String): Observable<RealmNotification> {
        return fetchCurrentUser()
                .concatMap {
                    currentUser ->
                    Observable.just(currentUser?.notifications?.first { it.id == notificationId })
                }
    }

    fun removeNotification(notification: RealmNotification): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.where(RealmNotification::class.java)
                                .equalTo("id", notification.id)
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
}
