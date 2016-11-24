package com.software.ssp.erkc.extensions

import com.software.ssp.erkc.data.realm.models.RealmCard
import com.software.ssp.erkc.data.rest.models.Card


fun RealmCard.toCard(): Card {
    return Card(
            this.id,
            this.name,
            this.statusId,
            this.remoteCardId,
            this.maskedCardNumber,
            this.statusStr)
}
