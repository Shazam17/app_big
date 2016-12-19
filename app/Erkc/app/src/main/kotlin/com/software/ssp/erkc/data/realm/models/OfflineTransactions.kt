package com.software.ssp.erkc.data.realm.models

import com.software.ssp.erkc.data.rest.models.Ipu
import io.realm.RealmList
import io.realm.RealmObject

/**
 * @author Alexander Popov on 14/12/2016.
 */
open class RealmOfflinePayment(
        open var login: String = "",
        open var receipt: RealmReceipt = RealmReceipt(),
        open var paymentSum: Double = 0.0,
        open var email: String = "",
        open var card: RealmCard? = RealmCard()
) : RealmObject()

open class RealmOfflineIpu(
        open var login: String = "",
        open var receipt: RealmReceipt = RealmReceipt(),
        open var ipuId: String = "",
        open var value: String = ""
) : RealmObject()

class IpuValueAndIpu(
        val ipuValues: List<RealmOfflineIpu>,
        val ipus: List<Ipu>
)