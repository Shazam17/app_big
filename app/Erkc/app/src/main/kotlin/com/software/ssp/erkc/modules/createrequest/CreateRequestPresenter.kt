package com.software.ssp.erkc.modules.createrequest

import com.software.ssp.erkc.common.mvp.RxPresenter
import javax.inject.Inject

class CreateRequestPresenter @Inject constructor(view: ICreateRequestView) : RxPresenter<ICreateRequestView>(view), ICreateRequestPresenter {

}