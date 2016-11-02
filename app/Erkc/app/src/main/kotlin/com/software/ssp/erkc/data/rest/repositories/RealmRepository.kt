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
        return Observable.create<OfflineUserSettings> { sub ->
            try {
                val settings: OfflineUserSettings
                val result = realm.where(OfflineUserSettings::class.java)
                        .equalTo("login", login)
                        .findAll().firstOrNull()

                if (result == null) {
                    settings = OfflineUserSettings(login)
                }
                else{
                    settings = realm.copyFromRealm(result)
                }

                sub.onNext(settings)
                sub.onCompleted()
            } catch (throwable: Throwable) {
                sub.onError(throwable)
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