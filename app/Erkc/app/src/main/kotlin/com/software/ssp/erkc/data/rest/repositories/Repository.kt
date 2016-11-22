package com.software.ssp.erkc.data.rest.repositories

import com.software.ssp.erkc.Constants
import com.software.ssp.erkc.extensions.md5
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

abstract class Repository {
    protected fun <T> applySchedulers(): Observable.Transformer<T, T> {
        return Observable.Transformer<T, T> { observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

/*    protected fun getSig(parameters: Map<String, String>): String {
        val private_key = Constants.API_SIG_PRIVATE_KEY
        var params = ""
        val tokenValue = parameters["token"]
        for ((param, value) in parameters) {
            params += "$param=$value"
        }
        val sig = (tokenValue + params + private_key).md5()
        return sig
    }*/
}

