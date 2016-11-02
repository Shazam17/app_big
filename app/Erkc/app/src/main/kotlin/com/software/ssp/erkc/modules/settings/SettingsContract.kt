package com.software.ssp.erkc.modules.settings

import com.software.ssp.erkc.common.mvp.IPresenter
import com.software.ssp.erkc.common.mvp.IView
import com.software.ssp.erkc.data.realm.models.OfflineSettings


interface ISettingsView : IView {
    fun setOfflineModeSwitch(checked: Boolean)
    fun setPushSwitch(checked: Boolean)
    fun setOperationStatusSwitch(checked: Boolean)
    fun setNewsSwitch(checked: Boolean)
    fun setPaymentSwitch(checked: Boolean)
    fun setIpuSwitch(checked: Boolean)

    fun setOfflinePasswordVisibility(visible: Boolean)
    fun navigateToOfflinePasswordScreen()
    fun showData(settings: OfflineSettings)
}

interface ISettingsPresenter : IPresenter<ISettingsView> {
    fun onOfflineModeSwitch(checked: Boolean)
    fun onPasswordButtonClick()
    fun onPushSwitch(checked: Boolean)
    fun onOperationStatusSwitch(checked: Boolean)
    fun onNewsSwitch(checked: Boolean)
    fun onPaymentSwitch(checked: Boolean)
    fun onIpuSwitch(checked: Boolean)
    fun onPause(paused: Boolean)

}