package com.software.ssp.erkc.modules.request.nonauthedrequest

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject

class RequestNonAuthPresenter @Inject constructor(view: IRequestNonAuthView) : RxPresenter<IRequestNonAuthView>(view), IRequestNonAuthPresenter {

}