package com.software.ssp.erkc.modules.settings

import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmSettings
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.SettingsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SettingsPresenter @Inject constructor(view: ISettingsView) : RxPresenter<ISettingsView>(view), ISettingsPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var settingsRepository: SettingsRepository

    private lateinit var offlineUserSettings: RealmSettings

    private var isChanged = false

    override fun onViewAttached() {
        fetchData()
    }

    override fun dropView() {
        view = null
        if (isChanged) {
            saveSettings()
        } else {
            onViewDetached()
        }
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
        if (!checked) {
            view?.setIpuSwitch(checked)
            view?.setNewsSwitch(checked)
            view?.setPaymentSwitch(checked)
            view?.setOperationStatusSwitch(checked)
        }
        offlineUserSettings.pushEnabled = checked
        updateData()
    }

    override fun onOperationStatusSwitch(checked: Boolean) {
        offlineUserSettings.operationStatusNotificationEnabled = checked
        updateData()
        isChanged = true
    }

    override fun onNewsSwitch(checked: Boolean) {
        offlineUserSettings.newsNotificationEnabled = checked
        updateData()
        isChanged = true
    }

    override fun onPaymentSwitch(checked: Boolean) {
        offlineUserSettings.paymentNotificationEnabled = checked
        updateData()
        isChanged = true
    }

    override fun onIpuSwitch(checked: Boolean) {
        offlineUserSettings.ipuNotificationEnabled = checked
        updateData()
        isChanged = true
    }

    private fun saveSettings() {
        offlineUserSettings.apply {
            subscriptions += settingsRepository.setStatusOperations(operationStatusNotificationEnabled)
                    .concatMap {
                        settingsRepository.setGetNews(newsNotificationEnabled)
                    }
                    .concatMap {
                        settingsRepository.setNeedToPay(paymentNotificationEnabled)
                    }
                    .concatMap {
                        settingsRepository.setNeedToSendMeters(ipuNotificationEnabled)
                    }
                    .subscribe(
                            {
                                result ->
                                onViewDetached()
                            },
                            {
                                error ->
                                onViewDetached()
                            })
        }
    }

    private fun fetchData() {
        subscriptions += realmRepository.fetchCurrentUser()
                .subscribe(
                        {
                            currentUser ->
                            offlineUserSettings = currentUser.settings!!
                            view?.showData(offlineUserSettings)
                            view?.setupInitialState(!activeSession.isOfflineSession)
                        },
                        {
                            error ->
                            view?.showMessage(error.parsedMessage())
                        }
                )
    }

    private fun updateData() {
        offlineUserSettings.apply {
            view?.setPushSwitch(
                    paymentNotificationEnabled
                            || newsNotificationEnabled
                            || ipuNotificationEnabled
                            || operationStatusNotificationEnabled
            )
        }
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
