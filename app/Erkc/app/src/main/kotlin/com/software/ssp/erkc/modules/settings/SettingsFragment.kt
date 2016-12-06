package com.software.ssp.erkc.modules.settings

import android.os.Bundle
import android.view.*
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpFragment
import com.software.ssp.erkc.data.realm.models.OfflineUserSettings
import com.software.ssp.erkc.di.AppComponent
import com.software.ssp.erkc.modules.settings.offlinepassword.OfflinePasswordActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import org.jetbrains.anko.onCheckedChange
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class SettingsFragment : MvpFragment(), ISettingsView {

    @Inject lateinit var presenter: ISettingsPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater!!.inflate(R.layout.fragment_settings, container, false)
    }

    override fun injectDependencies(appComponent: AppComponent) {
        DaggerSettingsComponent.builder()
                .appComponent(appComponent)
                .settingsModule(SettingsModule(this))
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached()
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun setOfflinePasswordVisibility(visible: Boolean) {
        passwordButtonTextView.visibility = if(visible) View.VISIBLE else View.INVISIBLE
        passwordHeaderTextView.visibility = if(visible) View.VISIBLE else View.INVISIBLE
    }

    override fun setOfflineModeSwitch(checked: Boolean) {
        offlineModeSwitch.isChecked = checked
    }

    override fun onPause() {
        presenter.saveSettings()
        super.onPause()
    }

    override fun setPushSwitch(checked: Boolean) {
        pushSwitch.isChecked = checked
    }

    override fun setOperationStatusSwitch(checked: Boolean) {
        operationStatusSwitch.isChecked = checked
    }

    override fun setNewsSwitch(checked: Boolean) {
        newsSwitch.isChecked = checked
    }

    override fun setPaymentSwitch(checked: Boolean) {
        payNotesSwitch.isChecked = checked
    }

    override fun setIpuSwitch(checked: Boolean) {
        ipuNotesSwitch.isChecked = checked
    }

    override fun navigateToOfflinePasswordScreen() {
        startActivity<OfflinePasswordActivity>()
    }

    override fun showData(userSettings: OfflineUserSettings) {
        offlineModeSwitch.isChecked = userSettings.offlineModeEnabled
        setOfflinePasswordVisibility(userSettings.offlineModeEnabled)

        pushSwitch.isChecked = userSettings.pushEnabled
        operationStatusSwitch.isChecked = userSettings.operationStatusNotificationEnabled
        newsSwitch.isChecked = userSettings.newsNotificationEnabled
        payNotesSwitch.isChecked = userSettings.paymentNotificationEnabled
        ipuNotesSwitch.isChecked = userSettings.ipuNotificationEnabled
    }

    override fun setupInitialState() {
        offlineModeSwitch.onCheckedChange { compoundButton, checked -> presenter.onOfflineModeSwitch(checked)  }
        passwordButtonTextView.onClick { presenter.onPasswordButtonClick() }
        pushSwitch.onCheckedChange { compoundButton, checked -> presenter.onPushSwitch(checked)  }
        operationStatusSwitch.onCheckedChange { compoundButton, checked -> presenter.onOperationStatusSwitch(checked)  }
        newsSwitch.onCheckedChange { compoundButton, checked -> presenter.onNewsSwitch(checked)  }
        payNotesSwitch.onCheckedChange { compoundButton, checked -> presenter.onPaymentSwitch(checked)  }
        ipuNotesSwitch.onCheckedChange { compoundButton, checked -> presenter.onIpuSwitch(checked)  }
    }
}
