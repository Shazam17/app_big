package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.AccountDataSource
import com.software.ssp.erkc.data.rest.models.User
import rx.Observable
import javax.inject.Inject


class AccountRepository @Inject constructor(private val accountDataSource: AccountDataSource) : Repository(){

    fun fetchUserInfo(token: String): Observable<User> {
        return accountDataSource
                .fetchUserInfo(token)
                .compose(this.applySchedulers<User>())
    }
}