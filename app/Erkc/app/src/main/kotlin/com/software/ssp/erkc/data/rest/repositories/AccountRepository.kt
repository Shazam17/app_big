package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.AccountDataSource
import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.models.DataResponse
import com.software.ssp.erkc.data.rest.models.User
import rx.Observable
import javax.inject.Inject


class AccountRepository @Inject constructor(private val accountDataSource: AccountDataSource) : Repository(){

    fun fetchUserInfo(token: String): Observable<DataResponse<User>> {
        return accountDataSource
                .fetchUserInfo(token)
                .compose(this.applySchedulers<DataResponse<User>>())
    }

    fun updateUserInfo(token: String, name: String, email: String): Observable<ApiResponse> {
        return accountDataSource
                .updateUserInfo(token, name, email)
                .compose(this.applySchedulers<ApiResponse>())
    }
}