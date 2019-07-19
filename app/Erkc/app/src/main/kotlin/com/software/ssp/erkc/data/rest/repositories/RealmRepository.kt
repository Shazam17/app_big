package com.software.ssp.erkc.data.rest.repositories


import android.content.Context
import com.software.ssp.erkc.data.realm.models.*
import com.software.ssp.erkc.data.rest.models.*
import com.software.ssp.erkc.common.NaturalOrderComparator
import com.software.ssp.erkc.data.realm.models.Initiator
import com.software.ssp.erkc.extensions.folderPath
import com.software.ssp.erkc.extensions.iconPath
import io.realm.Realm
import io.realm.RealmList
import rx.Observable
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class RealmRepository @Inject constructor(private val realm: Realm) : Repository() {

    fun close() {
        realm.close()
    }

    private fun fetchIpuDictionary() = realm
            .where(RealmIpuDictionary::class.java)
            .findAll()
            .first()

    fun fetchIpuLocations() = fetchIpuDictionary().locations
    fun fetchIpuServiceNames() = fetchIpuDictionary().service_names
    fun fetchIpuCheckIntervals() = fetchIpuDictionary().check_intervals
    fun fetchIpuTypes() = fetchIpuDictionary().types
    fun fetchIpuTypesTariff() = fetchIpuDictionary().tariff_types
    fun fetchIpuStatuses() = fetchIpuDictionary().statuses
    fun fetchIpuCloseReasons() = fetchIpuDictionary().close_reasons

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

    /*
        fun saveServiceTypes(list: List<ServiceType>): Observable<Boolean> {
            return Observable.create<Boolean>{ sub ->
                realm.executeTransactionAsync(
                        {
                            it.delete(RealmServiceType::class.java)
                            it.copyToRealm(list.map { RealmServiceType(
                                    it.id,
                                    it.name,
                                    it.service_code,
                                    it.icon
                            ) })
                        },
                        {
                            sub.onNext(true)
                        },
                        {
                            error->sub.onError(error)
                        }
                )}
        }
    */
    fun saveServiceTypes(context: Context, list: List<ServiceType>): Boolean {
        fun saveToFile(service: ServiceType) {
            val fos = FileOutputStream(File(service.iconPath(context)))
            try {
                fos.write(service.icon)
            } finally {
                fos.close()
            }
        }

        realm.executeTransaction {
            it.delete(RealmServiceType::class.java)
            it.copyToRealm(list.map {
                RealmServiceType(
                        it.id,
                        it.name,
                        it.service_code,
                        it.icon
                )
            })
        }

        File(list.firstOrNull()?.folderPath(context)).mkdirs()
        list.forEach { saveToFile(it) }

        return true
    }


    fun fetchOneServiceType(): RealmServiceType? {
        return realm
                .where(RealmServiceType::class.java)
                .findAll()
                .firstOrNull()
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

    fun saveIpuDictionary(dictionary: List<IpuDictionary>): Observable<Boolean> {
        fun toListIfExists(list: List<RealmIdName>, array: Array<IdName>): List<RealmIdName> {
            if (array.size > 0) {
                return array
                        .filter { !it.name.toLowerCase().contains("не указано") } //bad value, API doesn't accept it
                        .map { id_name -> RealmIdName(id_name.id, id_name.name) }
            }
            return list
        }

        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    {
                        it.delete(RealmIpuDictionary::class.java)

                        var locations = listOf<RealmIdName>()
                        var service_names = listOf<RealmIdName>()
                        var check_intervals = listOf<RealmIdName>()
                        var types = listOf<RealmIdName>()
                        var tariff_types = listOf<RealmIdName>()
                        var statuses = listOf<RealmIdName>()
                        var close_reasons = listOf<RealmIdName>()

                        for (d in dictionary) {
                            locations = toListIfExists(locations, d.locations)
                            service_names = toListIfExists(service_names, d.service_names)
                            check_intervals = toListIfExists(check_intervals, d.check_intervals)
                            types = toListIfExists(types, d.types)
                            tariff_types = toListIfExists(tariff_types, d.tariff_types)
                            statuses = toListIfExists(statuses, d.statuses)
                            close_reasons = toListIfExists(close_reasons, d.close_reasons)
                        }

                        it.copyToRealm(RealmIpuDictionary(
                                locations = RealmList(*locations.toTypedArray()),
                                service_names = RealmList(*service_names.toTypedArray()),
                                check_intervals = RealmList(*check_intervals.toTypedArray()),
                                types = RealmList(*types.toTypedArray()),
                                tariff_types = RealmList(*tariff_types.toTypedArray()),
                                statuses = RealmList(*statuses.toTypedArray()),
                                close_reasons = RealmList(*close_reasons.toTypedArray())
                        ))
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
                .concatMap { currentUser ->
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
                .flatMap { result ->
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
                .concatMap { currentUser ->
                    realm.where(RealmOfflineIpu::class.java)
                            .equalTo("login", currentUser.login)
                            .equalTo("receipt.id", receiptId)
                            .findAll()
                            .asObservable()
                            .filter { it.isLoaded }
                            .first()
                }
                .flatMap { result ->
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
                .concatMap { currentUser ->
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
                .concatMap { currentUser ->
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
                .concatMap { currentUser ->
                    currentUser.settings?.apply {
                        operationStatusNotificationEnabled = settings.operationsNotificationStatus == 1
                        newsNotificationEnabled = settings.newsNotificationStatus == 1
                        paymentNotificationEnabled = settings.paymentRemindStatus == 1
                        ipuNotificationEnabled = settings.ipuRemindStatus == 1

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
                        comimssionAgreed = receipt.comimssionAgreed
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
                    { error ->
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
                                it.comimssionAgreed,
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
                .concatMap { currentUser ->
                    currentUser?.receipts?.sortWith(object : NaturalOrderComparator<RealmReceipt>() {
                        override fun getString(p0: RealmReceipt): String {
                            return p0.address
                        }
                    })
                    Observable.just(currentUser?.receipts)
                }
    }

    fun fetchReceiptsById(receiptId: String): Observable<RealmReceipt> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
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
                .concatMap { currentUser ->
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
                .concatMap { currentUser ->

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
                        comimssionAgreed = paymentInfo.comimssionAgreed
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
                                { error ->
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

                        var receipt: RealmReceipt
                        try {
                            receipt = realm.copyFromRealm(realm.where(RealmReceipt::class.java).equalTo("id", it.receiptId).findFirst())
                                    ?: RealmReceipt()
                        } catch (e: IllegalArgumentException) {
                            receipt = RealmReceipt()
                        }


                        RealmPaymentInfo(
                                it.id,
                                it.date,
                                it.house ?: "",
                                it.status,
                                it.street ?: "",
                                it.comimssionAgreed ?: "",
                                it.barcode ?: "",
                                it.operationId ?: "",
                                it.modeId,
                                it.sum,
                                it.supplierName ?: "",
                                it.serviceName ?: "",
                                it.amount,
                                it.text ?: "",
                                it.address ?: "",
                                it.apart ?: "",
                                it.checkFile ?: "",
                                it.errorDesc ?: "",
                                receipt,
                                receipt.linkedCard ?: RealmCard()
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
                .concatMap { currentUser ->
                    Observable.just(currentUser.paymentsInfo)
                }
    }

    fun fetchPaymentInfoById(id: String): Observable<RealmPaymentInfo> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
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
                                { error ->
                                    sub.onError(error)
                                }
                        )
                    }
                }
    }

    fun deleteOfflinePayment(payment: RealmOfflinePayment): Observable<Boolean> {
        return fetchCurrentUser().concatMap { currentUser ->
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
                .concatMap { currentUser ->

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
                    { error ->
                        sub.onError(error)
                    }
            )
        }
    }

    fun fetchIpuList(): Observable<List<RealmIpu>> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
                    Observable.just(currentUser.ipus)
                }
    }

    fun fetchIpuByReceiptId(receiptId: String): Observable<RealmIpu> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
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
                .concatMap { currentUser ->

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
                .concatMap { realmIpu ->

                    realmIpu.apply {
                        ipuValues.clear()
                        ipuValues.addAll(ipus.map({
                            RealmIpuValue(
                                    id = it.id,
                                    serviceName = it.serviceName,
                                    shortName = it.shortName,
                                    number = it.number,
                                    installPlace = it.installPlace,
                                    date = it.date,
                                    value = it.value,
                                    isSent = true,
                                    userRegistered = it.user_registered?.equals("1") ?: false
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
                    { error ->
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
                .concatMap { currentUser ->
                    Observable.just(currentUser?.notifications?.sortedByDescending { it.deliveredDate })
                }
    }

    fun fetchNotificationById(notificationId: String): Observable<RealmNotification> {
        return fetchCurrentUser()
                .concatMap { currentUser ->
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

    fun saveRequestList(request: List<Request>): Observable<Boolean> {
//        return fetchRequestList()
//                .concatMap {
        return Observable.create<Boolean> { sub ->
            val realmRequestList = RealmList<RealmRequest>()
            request.forEach {
                val tasksList = RealmList<RealmRequestTask>()
                val comment = RealmList<RealmComment>()
                it.tasks?.forEach { it ->
                    tasksList.add(RealmRequestTask(
                            id = it
                    ))
                }
                it.comments?.forEach { value ->
                    comment.add(RealmComment(
                            id = value.id!!,
                            created_at = value.created_at,
                            initiator = Initiator(
                                    id = value.initiator!!.id,
                                    username = value.initiator.username,
                                    name = value.initiator.name,
                                    phone = value.initiator.phone,
                                    info = value.initiator.info
                            ),
                            message = value.message,
                            filename = value.filename,
                            filetype = value.filetype,
                            downloadLink = value.downloadLink
                    ))
                }
                realmRequestList.add(RealmRequest(
                        id = it.id,
                        created_at = it.created_at,
                        company = RealmCompany(
                                id = it.company?.id,
                                type = it.company?.type,
                                typeLabel = it.company?.typeLabel,
                                state = it.company?.state,
                                stateLabel = it.company?.stateLabel,
                                name = it.company?.name,
                                full_name = it.company?.full_name,
                                jur_address = it.company?.jur_address,
                                fact_address = it.company?.fact_address,
                                inn = it.company?.inn,
                                kpp = it.company?.kpp,
                                ogrn = it.company?.ogrn,
                                email = it.company?.email,
                                phone = it.company?.phone,
                                phone_number = it.company?.phone_number
                        ),
                        type = RealmTypeRequest(
                                id = it.type?.id,
                                name = it.type?.name
                        ),
                        state = RealmStateRequest(
                                id = it.state?.id,
                                name = it.state?.name,
                                sort = it.state?.sort,
                                process_state = it.state?.process_state,
                                stateLabel = it.state?.stateLabel
                        ),
                        applicant = it.applicant,
                        house = RealmHouseRequest(
                                id = it.house?.id,
                                company_id = it.house?.company_id,
                                code = it.house?.code,
                                address = it.house?.address,
                                fias = it.house?.fias,
                                cadastral_number = it.house?.cadastral_number
                        ),
                        premise = RealmPremiseRequest(
                                id = it.premise?.id,
                                house_id = it.premise?.house_id,
                                number = it.premise?.number
                        ),
                        chanel = it.chanel,
                        chanelLabel = it.chanelLabel,
                        name = it.name,
                        message = it.message,
                        contact = it.contact,
                        code = it.code,
                        is_overdue = it.is_overdue,
                        comment = comment,
                        task = tasksList
                ))


            }
            realm.executeTransactionAsync(
                    {
                        it.copyToRealmOrUpdate(realmRequestList)
                    },
                    {
                        sub.onNext(true)
                    },
                    { error ->
                        sub.onError(error)
                    })
        }
    }

    fun fetchRequestList(): Observable<List<RealmRequest>> {
        return Observable.just(
                realm.where(RealmRequest::class.java)
                        .findAll()
        )
    }

    fun fetchRequestById(id: Int): Observable<RealmRequest> {
        return Observable.just(
                realm.where(RealmRequest::class.java)
                        .findAll()
                        .first { it.id == id }
        )
    }

    private fun deleteRequestList(): Observable<Boolean> {
        return Observable.create<Boolean> {
            realm.executeTransactionAsync {
                it.where(RealmRequest::class.java)
                        .findAll()
                        .deleteAllFromRealm()
            }
        }
    }
}