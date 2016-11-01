package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.AppPrefs
import com.software.ssp.erkc.data.db.AddressCache
import com.software.ssp.erkc.data.rest.models.Address
import io.realm.Realm
import io.realm.RealmResults
import rx.Observable
import java.util.*
import javax.inject.Inject

/**
 * @author Alexander Popov on 31/10/2016.
 */
class RealmRepository @Inject constructor(private val realm: Realm) : Repository() {

    fun getAllAddresses(): Observable<RealmResults<AddressCache>> {
        val results = realm
                .where(AddressCache::class.java)
                .findAllAsync()
                .asObservable()
        return results
    }

    fun getAllAddressesByQuery(query: String): Observable<RealmResults<AddressCache>> {
        val results = realm
                .where(AddressCache::class.java)
                .contains("query", query.toLowerCase())
                .findAllAsync()
                .asObservable()
        return results
    }

    fun saveAddressesList(addresses: List<Address>) {
        val cacheAddresses = arrayListOf<AddressCache>()
        for ((id, name) in addresses) {
            cacheAddresses.add(AddressCache(id, name, name.toLowerCase()))
        }
        realm.executeTransaction {
            realm.deleteAll()
            realm.copyToRealm(cacheAddresses)
        }
        AppPrefs.lastCashingDate = Date().time
    }

    fun close() {
        realm.close()
    }
}