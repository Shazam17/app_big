package com.software.ssp.erkc.modules.settings

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.OfflineUserSettings
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SettingsPresenter @Inject constructor(view: ISettingsView) : RxPresenter<ISettingsView>(view), ISettingsPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var activeSession: ActiveSession

    private lateinit var offlineUserSettings: OfflineUserSettings

    override fun onViewAttached() {
        fetchData()
    }

    override fun onViewDetached() {
        realmRepository.close()
        super.onViewDetached()
    }

    override fun onPasswordButtonClick() {
        view?.navigateToOfflinePasswordScreen()
    }

    override fun onOfflineModeSwitch(checked: Boolean) {
        offlineUserSettings.offlineModeEnabled = checked
        view?.setOfflinePasswordVisibility(checked)
        updateData()
    }

    override fun onPushSwitch(checked: Boolean) {
        offlineUserSettings.pushEnabled = checked
        updateData()
    }

    override fun onOperationStatusSwitch(checked: Boolean) {
        offlineUserSettings.operationStatusNotificationEnabled = checked
        updateData()
    }

    override fun onNewsSwitch(checked: Boolean) {
        offlineUserSettings.newsNotificationEnabled = checked
        updateData()
    }

    override fun onPaymentSwitch(checked: Boolean) {
        offlineUserSettings.paymentNotificationEnabled = checked
        updateData()
    }

    override fun onIpuSwitch(checked: Boolean) {
        offlineUserSettings.ipuNotificationEnabled = checked
        updateData()
    }

    private fun fetchData() {
        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            offlineUserSettings = currentUser.settings!!
                            view?.showData(offlineUserSettings)
                            view?.setupInitialState()
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun updateData() {
        subscriptions += realmRepository.updateOfflineSettings(offlineUserSettings)
                .subscribe(
                {},
                {
                    error ->
                    view?.showMessage(error.parsedMessage())
                }
        )
    }
}
