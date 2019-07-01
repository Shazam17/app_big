package com.software.ssp.erkc.modules.request.nonauthedrequest

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IRequestNonAuthView: IView {
    fun showSupportInfo()
    fun setSupportInfo(nameManagerCompany: String, placeCompany: String, numberPhone: String)
}


interface IRequestNonAuthPresenter: IPresenter<IRequestNonAuthView> {

}