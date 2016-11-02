package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.realm.models.OfflineUserSettings
import com.software.ssp.erkc.data.rest.models.Address
import com.software.ssp.erkc.data.rest.models.AddressCache
import io.realm.Realm
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 31/10/2016.
 */
class RealmRepository @Inject constructor(val realm: Realm) : Repository() {

    fun getAllAdresses(): List<AddressCache> {
        val results = realm.where(AddressCache::class.java).findAllAsync()
        realm.close()
        return results
    }

    fun getAllAddressesByQuesry(query: String): List<AddressCache> {
        val results = realm.where(AddressCache::class.java).contains("query", query.toLowerCase()).findAllAsync()
        realm.close()
        return results
    }

    fun initByAddresses(addresses: List<Address>) {
        val cacheAddresses = arrayListOf<AddressCache>()
        for ((id, name) in addresses) {
            cacheAddresses.add(AddressCache(id, name, name.toLowerCase()))
        }
        realm.executeTransaction {
            realm.deleteAll()
            realm.copyToRealm(cacheAddresses)
        }
    }

    fun fetchOfflineSettings(login: String): Observable<OfflineUserSettings> {

        return realm
                .where(OfflineUserSettings::class.java)
                .equalTo("login", login)
                .findAllAsync()
                .asObservable()
                .flatMap { results ->
                    val firstResult = results.firstOrNull()
                    if(firstResult == null){
                        Observable.just(OfflineUserSettings(login))
                    }
                    else{
                        Observable.just(realm.copyFromRealm(firstResult))
                    }
                }
    }

    fun updateOfflineSettings(userSettings: OfflineUserSettings): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            realm.executeTransactionAsync(
                    { it.copyToRealmOrUpdate(userSettings) },
                    {
                        sub.onNext(true)
                        sub.onCompleted()
                    },
                    { throwable ->
                        sub.onError(throwable)
                    }
            )
        }
    }
}