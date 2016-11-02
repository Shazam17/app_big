package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.realm.models.OfflineSettings
import com.software.ssp.erkc.data.rest.models.Address
import com.software.ssp.erkc.data.rest.models.AddressCache
import io.realm.Realm
import io.realm.RealmConfiguration
import rx.Observable
import javax.inject.Inject

/**
 * @author Alexander Popov on 31/10/2016.
 */
class RealmRepository @Inject constructor() : Repository() {

    private fun getRealm(): Realm {
        val realmConfiguration = RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build()

        return Realm.getInstance(realmConfiguration)
    }

    private fun getRealmObservable(): Observable<Realm> {
        var realm: Realm? = null
        return Observable.create<Realm> {
            subscriber ->
            realm = getRealm()
            subscriber.onNext(realm)
            subscriber.onCompleted()
        }
    }


    fun getAllAdresses(): List<AddressCache> {
        val realm = getRealm()
        val results = realm.where(AddressCache::class.java).findAllAsync()
        realm.close()
        return results
    }

    fun getAllAddressesByQuesry(query: String): List<AddressCache> {
        val realm = getRealm()
        val results = realm.where(AddressCache::class.java).contains("query", query.toLowerCase()).findAllAsync()
        realm.close()
        return results
    }

    fun initByAddresses(addresses: List<Address>) {
        val realm = getRealm()
        val cacheAddresses = arrayListOf<AddressCache>()
        for ((id, name) in addresses) {
            cacheAddresses.add(AddressCache(id, name, name.toLowerCase()))
        }
        realm.executeTransaction {
            realm.deleteAll()
            realm.copyToRealm(cacheAddresses)
        }
    }

    fun fetchOfflineSettings(login: String): OfflineSettings {
        val settings = getRealm().where(OfflineSettings::class.java).equalTo("login", login).findAll().firstOrNull()

        if (settings == null) {
            return OfflineSettings(login)
        } else {
            return getRealm().copyFromRealm(settings)
        }
    }

    fun updateOfflineSettings(settings: OfflineSettings): Observable<Boolean> {
        return getRealmObservable()
                .flatMap { realm ->
                    realm.beginTransaction()
                    realm.copyToRealmOrUpdate(settings)
                    realm.commitTransaction()
                    realm.close()
                    Observable.just(true)
                }.compose(applySchedulers<Boolean?>())
    }
}