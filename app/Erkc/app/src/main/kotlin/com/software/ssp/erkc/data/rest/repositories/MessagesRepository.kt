package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.MessagesDataSource
import com.software.ssp.erkc.data.rest.models.Notification
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject


class MessagesRepository @Inject constructor(private val messagesDataSource: MessagesDataSource) : Repository() {

    fun fetchMessagesByUser(): Observable<List<Notification>> {
        return messagesDataSource
                .getMessagesByUser()
                .compose(this.applySchedulers<List<Notification>>())
    }

    fun fetchMessageById(id: String): Observable<Notification> {
        return messagesDataSource
                .getMessageById(id)
                .compose(this.applySchedulers<Notification>())
    }

    fun setMessageAsRead(id: String): Observable<ResponseBody> {
        return messagesDataSource
                .setMessageRead(id)
                .compose(this.applySchedulers<ResponseBody>())
    }

    fun deleteMessage(id: String): Observable<ResponseBody> {
        return messagesDataSource
                .deleteMessage(id)
                .compose(this.applySchedulers<ResponseBody>())
    }
}
