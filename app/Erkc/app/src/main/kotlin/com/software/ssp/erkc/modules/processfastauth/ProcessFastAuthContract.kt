package com.software.ssp.erkc.modules.processfastauth

import android.content.Context
import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView


interface IProcessFastAuthView : IView {
    fun setPin(pin: String)
    fun finishFastAuthActivity()
}

interface IProcessFastAuthPresenter : IPresenter<IProcessFastAuthView> {
    fun onBackPressed()
    fun onChangeUserClick()
}
