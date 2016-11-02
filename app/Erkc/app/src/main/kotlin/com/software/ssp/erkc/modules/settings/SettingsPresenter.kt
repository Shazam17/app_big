package com.software.ssp.erkc.modules.settings

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.OfflineSettings
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SettingsPresenter @Inject constructor(view: ISettingsView) : RxPresenter<ISettingsView>(view), ISettingsPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var activeSession: ActiveSession

    private lateinit var login: String
    private lateinit var offlineSettings: OfflineSettings

    private var isViewUpdating = false

    override fun onViewAttached() {
        login = activeSession.user!!.login
        fetchData()
    }

    override fun onPause(paused: Boolean) {
        isViewUpdating = paused
    }

    override fun onPasswordButtonClick() {
        view?.navigateToOfflinePasswordScreen()
    }

    override fun onOfflineModeSwitch(checked: Boolean) {
        tryToUpdateSettings {
            offlineSettings.offlineModeEnabled = checked
            view?.setOfflinePasswordVisibility(checked)
        }
    }

    override fun onPushSwitch(checked: Boolean) {
        tryToUpdateSettings {
            offlineSettings.pushEnabled = checked
        }
    }

    override fun onOperationStatusSwitch(checked: Boolean) {
        tryToUpdateSettings {
            offlineSettings.operationStatusNotificationEnabled = checked
        }
    }

    override fun onNewsSwitch(checked: Boolean) {
        tryToUpdateSettings {
            offlineSettings.newsNotificationEnabled = checked
        }
    }

    override fun onPaymentSwitch(checked: Boolean) {
        tryToUpdateSettings {
            offlineSettings.paymentNotificationEnabled = checked
        }
    }

    override fun onIpuSwitch(checked: Boolean) {
        tryToUpdateSettings{
            offlineSettings.ipuNotificationEnabled = checked
        }
    }



    private fun tryToUpdateSettings(updateFunc:()->Unit){
        if(isViewUpdating) return
        updateFunc()
        updateData()
    }

    private fun fetchData() {
        offlineSettings = realmRepository.fetchOfflineSettings(login)
        isViewUpdating = true
        view?.showData(offlineSettings)
        isViewUpdating = false
    }

    private fun updateData(){
        subscriptions += realmRepository.updateOfflineSettings(offlineSettings).subscribe(
                { response ->  },
                { throwable -> view?.showMessage(throwable.parsedMessage()) }
        )
    }

}
