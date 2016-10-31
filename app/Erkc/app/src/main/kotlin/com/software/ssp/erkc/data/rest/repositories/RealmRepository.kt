package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.models.Address
import com.software.ssp.erkc.data.rest.models.AddressCache
import io.realm.Realm
import javax.inject.Inject

/**
 * @author Alexander Popov on 31/10/2016.
 */
class RealmRepository @Inject constructor() : Repository() {

    fun getAllAdresses(): List<AddressCache> {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(AddressCache::class.java).findAllAsync()
        realm.close()
        return results
    }

    fun getAllAddressesByQuesry(query: String): List<AddressCache> {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(AddressCache::class.java).contains("query", query.toLowerCase()).findAllAsync()
        realm.close()
        return results
    }

    fun initByAddresses(addresses: List<Address>) {
        val realm = Realm.getDefaultInstance()
        val cacheAddresses = arrayListOf<AddressCache>()
        for ((id, name) in addresses) {
            cacheAddresses.add(AddressCache(id, name, name.toLowerCase()))
        }
        realm.executeTransaction {
            realm.deleteAll()
            realm.copyToRealm(cacheAddresses)
        }
    }
}