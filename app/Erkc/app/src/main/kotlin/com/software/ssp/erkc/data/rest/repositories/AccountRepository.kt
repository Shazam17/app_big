package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.data.rest.datasource.AccountDataSource
import com.software.ssp.erkc.data.rest.models.ApiResponse
import com.software.ssp.erkc.data.rest.models.User
import rx.Observable
import javax.inject.Inject


class AccountRepository @Inject constructor(private val accountDataSource: AccountDataSource) : Repository() {

    fun fetchUserInfo(): Observable<User> {
        return accountDataSource
                .fetchUserInfo()
                .compose(this.applySchedulers<User>())
    }

    fun updateUserInfo(name: String, email: String, password: String, rePassword: String): Observable<ApiResponse> {
        val params = mutableMapOf(
                "name" to name,
                "email" to email
        )

        if (!password.isNullOrBlank()) {
            params.put("password", password)
            params.put("repassword", rePassword)
        }

        return accountDataSource
                .updateUserInfo(params)
                .compose(this.applySchedulers<ApiResponse>())
    }
}
