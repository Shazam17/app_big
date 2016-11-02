package com.software.ssp.erkc.modules.settings.offlinepassword

import android.os.Bundle
import android.view.MenuItem
import com.software.ssp.erkc.R
import com.software.ssp.erkc.common.mvp.MvpActivity
import com.software.ssp.erkc.di.AppComponent
import kotlinx.android.synthetic.main.activity_offline_password.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.textChangedListener
import javax.inject.Inject


class OfflinePasswordActivity : MvpActivity(), IOfflinePasswordView {

    @Inject lateinit var presenter: IOfflinePasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_password)

        initViews()
        presenter.onViewAttached()
    }

    override fun resolveDependencies(appComponent: AppComponent) {
        DaggerOfflinePasswordComponent.builder()
                .appComponent(appComponent)
                .offlinePasswordModule(OfflinePasswordModule(this))
                .build()
                .inject(this)
    }

    override fun beforeDestroy() {
        presenter.dropView()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun dismiss() {
        finish()
    }

    override fun showSecondPasswordError(errorResId: Int?) {
        if (errorResId == null) {
            offlinePassSecondInputLayout.error = null
        } else {
            offlinePassSecondInputLayout.error = getString(errorResId)
        }
    }

    override fun enableSendButton(enabled: Boolean) {
        offlinePassSaveButton.isEnabled = enabled
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f

        offlinePassFirstInputEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k -> presenter.onFirstInputChange(charSequence.toString()) }
        }

        offlinePassSecondInputEditText.textChangedListener {
            onTextChanged { charSequence, i, j, k -> presenter.onSecondInputChange(charSequence.toString()) }
        }

        offlinePassSaveButton.onClick { presenter.onSaveButtonClick() }

    }

}