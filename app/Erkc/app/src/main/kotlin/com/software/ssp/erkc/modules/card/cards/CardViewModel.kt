package com.software.ssp.erkc.modules.card.cards

import com.software.ssp.erkc.data.realm.models.RealmCard


class CardViewModel(
        val card: RealmCard,
        var isDeletePending: Boolean = false,
        var isRequestPending: Boolean = false
)
