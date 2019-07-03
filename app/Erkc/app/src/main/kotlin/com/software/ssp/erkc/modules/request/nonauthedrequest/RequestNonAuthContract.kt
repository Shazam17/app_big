package com.software.ssp.erkc.modules.request.nonauthedrequest

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView

interface IRequestNonAuthView: IView {
    fun showSupportInfo()
    fun setSupportInfo(nameManagerCompany: String, placeCompany: String, numberPhone: String)
    fun navigateToStreetSelectScreen()
    fun setStreetField(street: String)
    fun navigateToCallScreen()
}


interface IRequestNonAuthPresenter: IPresenter<IRequestNonAuthView> {
    fun onAddressClick()
    fun onStreetSelected(street: String)
    fun onCallButtonClick()
}