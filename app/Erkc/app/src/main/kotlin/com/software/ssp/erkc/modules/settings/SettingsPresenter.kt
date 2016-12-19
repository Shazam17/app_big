package com.software.ssp.erkc.modules.settings

import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.RxPresenter
import com.software.ssp.erkc.data.realm.models.RealmSettings
import com.software.ssp.erkc.data.rest.ActiveSession
import com.software.ssp.erkc.data.rest.repositories.RealmRepository
import com.software.ssp.erkc.data.rest.repositories.SettingsRepository
import com.software.ssp.erkc.extensions.parsedMessage
import rx.Observable
import rx.lang.kotlin.plusAssign
import javax.inject.Inject


class SettingsPresenter @Inject constructor(view: ISettingsView) : RxPresenter<ISettingsView>(view), ISettingsPresenter {

    @Inject lateinit var realmRepository: RealmRepository
    @Inject lateinit var activeSession: ActiveSession
    @Inject lateinit var settingsRepository: SettingsRepository

    private lateinit var offlineUserSettings: RealmSettings

    override fun onViewAttached() {
        fetchData()
    }

    override fun onViewDetached() {
        saveSettings()
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

    private fun saveSettings() {
        offlineUserSettings.apply {
            subscriptions += Observable.zip(
                    settingsRepository.setStatusOperations(operationStatusNotificationEnabled),
                    settingsRepository.setGetNews(newsNotificationEnabled),
                    settingsRepository.setNeedToPay(paymentNotificationEnabled),
                    settingsRepository.setNeedToSendMeters(ipuNotificationEnabled),
                    {
                        statusOperation, getNews, needToPay, needToSendMeters ->
                        Observable.just(null)
                    })
                    .subscribe(
                            {
                                result ->
                                view?.showMessage(R.string.settings_saved_message)
                            },
                            {
                                error ->
                                view?.showMessage(error.parsedMessage())
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
