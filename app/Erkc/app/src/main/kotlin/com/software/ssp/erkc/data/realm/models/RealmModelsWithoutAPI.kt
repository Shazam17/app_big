package com.software.ssp.erkc.data.realm.models

import com.software.ssp.erkc.R
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmRequestStatus(
        open var type: String? = "", //Ожидает рассмотрения, на рассмотрения, в работе, выполнена, отменена
        open var date: String? = "" // dd.MM.yyyy HH:mm
) : RealmObject() {
    fun getTitle(): String {
        return when (this.type) {
            RequestStatusTypes.PendingReview.name -> "Ожидает рассмотрения"
            RequestStatusTypes.OnReview.name -> "На рассмотрении"
            RequestStatusTypes.InWork.name -> "В работе"
            RequestStatusTypes.Complete.name -> "Выполнена"
            RequestStatusTypes.Canceled.name -> "Отменена"
            else -> "Отменена"
        }
    }
}

open class RealmPhotoPath(
        open var path: String? = null
) : RealmObject()

open class RealmChatMessage(
        open var text: String? = "",
        open var date: String? = "", // dd.MM.yyyy HH:mm
        open var writter: String? = ""
) : RealmObject()


enum class WritterTypes(name: String) {
    Dispatcher("Dispatcher"),
    User("User")
}

enum class RequestStatusTypes(name: String) {
    PendingReview("PendingReview"),
    OnReview("OnReview"),
    InWork("InWork"),
    Complete("Complete"),
    Canceled("Canceled")
}

enum class RequestTabTypes(name: String) {
    Archive("Archive"),
    Active("Active"),
    Draft("Draft")
}


/* Colors status */

enum class StatusColors(val id: String) {
    InWork("В работе"), Complete("Закрыта"),PendingReview("Новая");

    companion object {
        fun getColor(id: String): String {
            return when (id) {
                StatusColors.PendingReview.id -> return "#BA9B0B"
                StatusColors.InWork.id -> return "#258D09"
                StatusColors.Complete.id -> return "#2D5027"
                else -> "#909090"
            }
        }
    }
}

